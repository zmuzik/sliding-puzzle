package zmuzik.slidingpuzzle2.screens.game

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.screen_game.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.*
import zmuzik.slidingpuzzle2.screens.BaseFragment
import java.lang.ref.WeakReference

class GameFragment : BaseFragment(), GameScreen, ShakeDetector.OnShakeListener {

    private var boardWidth: Int = 0
    private var boardHeight: Int = 0

    val prefs by inject<Prefs>()
    val shakeDetector by inject<ShakeDetector>()
    val viewModel by viewModel<GameScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { viewModel.initFromIntent(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        board.gameScreen = WeakReference(this)
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.register()
        shakeDetector.setOnShakeListener(this)
        mainActivity?.showStatusBar(false)
        setupInitialThumbnailPos()
        viewModel.pictureUri?.let { loadPicture(it) }
                ?: kotlin.run { finishWithMessage(R.string.picture_not_supplied) }
    }

    private fun setupInitialThumbnailPos() {
        if (viewModel.storedBoardState != null) {
            thumbnail.hide()
            return
        }

        // initial position
        mainActivity?.thumbBitmap?.get()?.let { thumbnail.setImageBitmap(it) }
        (thumbnail.layoutParams as? ConstraintLayout.LayoutParams)?.let {
            it.width = viewModel.thumbnailDim
            it.height = viewModel.thumbnailDim
        }

        with(horizGuideline) {
            val lp = this.layoutParams as ConstraintLayout.LayoutParams
            lp.guideBegin = viewModel.thumbnailTop
            this.layoutParams = lp
        }

        with(vertGuideline) {
            val lp = this.layoutParams as ConstraintLayout.LayoutParams
            lp.guideBegin = viewModel.thumbnailLeft
            this.layoutParams = lp
        }
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.unRegister()
        board.saveGameState(viewModel)
    }

    override fun onShake() {
        hideShuffleIcon()
        board?.maybeShuffle()
    }

    fun finishWithMessage(stringId: Int) {
        activity?.toast(stringId)
        //finish()
    }

    fun loadPicture(uri: String) {
        Picasso.with(activity)
                .load(uri)
                .noFade()
                .resize(screenWidth, screenHeight)
                .centerInside()
                .into(imageTarget)
    }

    override fun hideShuffleIcon() {
        shuffleBtn.hide()
    }

    private fun adjustBoardDimensions(board: PuzzleBoardView, bitmap: Bitmap) {
        val screenSideRatio = screenWidth.toFloat() / screenHeight
        val origPictureSideRatio = bitmap.width.toFloat() / bitmap.height
        if (origPictureSideRatio > screenSideRatio) {
            boardWidth = screenWidth
            boardHeight = (screenWidth / origPictureSideRatio).toInt()
        } else {
            boardHeight = screenHeight
            boardWidth = (screenHeight * origPictureSideRatio).toInt()
        }
        val widthMultiple = if (boardWidth > boardHeight) prefs.gridDimLong else prefs.gridDimShort
        val heightMultiple = if (boardWidth > boardHeight) prefs.gridDimShort else prefs.gridDimLong

        boardWidth -= boardWidth % widthMultiple
        boardHeight -= boardHeight % heightMultiple

        board.setDimensions(boardWidth, boardHeight)
    }

    override fun onStop() {
        Picasso.with(activity).cancelRequest(imageTarget)
        super.onStop()
    }

    private var imageTarget: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            adjustBoardDimensions(board, bitmap)
            progressBar.hide()
            board.init(bitmap, viewModel)
            viewModel.storedBoardState?.let { boardState ->
                if (boardState == PuzzleBoardView.State.LOADING
                        || boardState == PuzzleBoardView.State.LOADED
                        || boardState == PuzzleBoardView.State.READY_TO_SHUFFLE) {
                    animateThumbnailToBoard(bitmap)
                } else {
                    showBoardWithoutAnimation()
                }
            } ?: kotlin.run {
                animateThumbnailToBoard(bitmap)
            }
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            findNavController().popBackStack()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    fun animateThumbnailToBoard(bitmap: Bitmap) {
        val smalledBitmapDim: Int = Math.min(bitmap.width, bitmap.height)
        val dest = ConstraintSet()
        dest.clone(gameScreenRoot)
        dest.clear(R.id.thumbnail)
        dest.centerHorizontally(R.id.thumbnail, 0)
        dest.centerVertically(R.id.thumbnail, 0)
        dest.constrainWidth(R.id.thumbnail, smalledBitmapDim)
        dest.constrainHeight(R.id.thumbnail, smalledBitmapDim)
        val transition = AutoTransition().also {
            it.duration = 300
            it.interpolator = AnticipateOvershootInterpolator(1.6f)
        }
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionEnd(transition: Transition) {
                thumbnail.hide()
                board.show()
                shuffleBtn.show()
                shuffleBtn.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake_anim))
            }
        })
        TransitionManager.beginDelayedTransition(gameScreenRoot, transition)
        dest.applyTo(gameScreenRoot)
    }

    fun showBoardWithoutAnimation() {
        thumbnail.hide()
        shuffleBtn.hide()
    }
}
