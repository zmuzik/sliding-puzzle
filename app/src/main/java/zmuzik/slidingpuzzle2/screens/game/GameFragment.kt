package zmuzik.slidingpuzzle2.screens.game

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.screen_game.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.*
import zmuzik.slidingpuzzle2.screens.MainActivity
import java.lang.ref.WeakReference

class GameFragment : Fragment(), GameScreen, ShakeDetector.OnShakeListener {

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var boardWidth: Int = 0
    private var boardHeight: Int = 0

    val prefs by inject<Prefs>()
    val shakeDetector by inject<ShakeDetector>()
    val viewModel by viewModel<GameScreenViewModel>()

    val mainActivity get() = activity as? MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { viewModel.initFromIntent(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        board.gameScreen = WeakReference(this)
        resolveScreenDimensions()
        viewModel.thumbnailDim?.let {
            thumbnail.layoutParams.width = it
            thumbnail.layoutParams.height = it
        }
        thumbnail.setImageBitmap(viewModel.thumbnailBitmap)
        viewModel.pictureUri?.let { loadPicture(it) }
                ?: kotlin.run { finishWithMessage(R.string.picture_not_supplied) }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.register()
        shakeDetector.setOnShakeListener(this)
        mainActivity?.showStatusBar(false)
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

    private fun resolveScreenDimensions() {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }

    fun getMaxScreenDim() = if (screenWidth > screenHeight) screenWidth else screenHeight

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
            thumbnail.hide()
            progressBar.hide()
            board.init(bitmap, viewModel)
            viewModel.storedBoardState?.let { boardState ->
                if (boardState == PuzzleBoardView.State.LOADING || boardState == PuzzleBoardView.State.LOADED) {
                    shuffleBtn.show()
                    shuffleBtn.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake_anim))
                } else {
                    shuffleBtn.hide()
                }
            } ?: kotlin.run {
                shuffleBtn.show()
                shuffleBtn.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake_anim))
            }
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            finishWithMessage(R.string.unable_to_load_flickr_picture)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
}
