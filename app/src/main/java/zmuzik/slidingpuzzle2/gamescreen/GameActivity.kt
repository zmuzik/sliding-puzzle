package zmuzik.slidingpuzzle2.gamescreen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils

import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

import javax.inject.Inject

import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_game.*
import zmuzik.slidingpuzzle2.App
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.Keys
import zmuzik.slidingpuzzle2.common.PreferencesHelper
import zmuzik.slidingpuzzle2.common.ShakeDetector
import zmuzik.slidingpuzzle2.common.Toaster
import zmuzik.slidingpuzzle2.flickr.FlickrApi

class GameActivity : Activity(), GameScreenView, ShakeDetector.OnShakeListener {

    internal val TAG = this.javaClass.simpleName

    internal var screenWidth: Int = 0
    internal var screenHeight: Int = 0
    internal var boardWidth: Int = 0
    internal var boardHeight: Int = 0

    @Inject
    lateinit var prefsHelper: PreferencesHelper
    @Inject
    lateinit var toaster: Toaster
    @Inject
    lateinit var flickrApi: FlickrApi
    @Inject
    lateinit var presenter: GameScreenPresenter
    @Inject
    lateinit var shakeDetector: ShakeDetector

    lateinit var component: GameActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(R.layout.activity_game)
        ButterKnife.bind(this)
        val intent = intent
        if (intent == null) {
            toaster.show(R.string.picture_not_supplied)
            finish()
        }

        setScreenOrientation(getIntent().extras.getBoolean(Keys.IS_HORIZONTAL))
        resolveScreenDimensions()

        progressBar.visibility = View.VISIBLE
        //mBoard.setVisibility(View.GONE);
        presenter.requestPictureUri(intent!!)

    }

    override fun onResume() {
        super.onResume()
        if (shakeDetector != null) {
            shakeDetector.register()
            shakeDetector.setOnShakeListener(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (shakeDetector != null) {
            shakeDetector.unRegister()
        }
    }

    override fun onShake() {
        shuffle(null)
    }

    override fun finishWithMessage(stringId: Int) {
        toaster.show(stringId)
        finish()
    }

    override fun loadPicture(uri: String) {
        Picasso.with(this)
                .load(uri)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_STORE)
                .resize(screenWidth, screenHeight)
                .centerInside()
                .into(mTarget)
    }

    internal fun inject() {
        component = DaggerGameActivityComponent.builder()
                .appComponent((application as App).getComponent(this))
                .gameActivityModule(GameActivityModule(this))
                .build()
        component.inject(this)
        component.inject(presenter)
    }

    internal fun setScreenOrientation(isHorizontal: Boolean) {
        requestedOrientation = if (isHorizontal)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    internal fun resolveScreenDimensions() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
    }

    override fun getMaxScreenDim() = if (screenWidth > screenHeight) screenWidth else screenHeight

    override fun hideShuffleIcon() {
        shuffleBtn.visibility = View.GONE
    }

    fun shuffle(v: View?) {
        hideShuffleIcon()
        board?.maybeShuffle()
    }

    internal fun adjustBoardDimensions(board: PuzzleBoardView, bitmap: Bitmap) {
        val screenSideRatio = screenWidth.toFloat() / screenHeight
        val origPictureSideRatio = bitmap.width.toFloat() / bitmap.height
        if (origPictureSideRatio > screenSideRatio) {
            boardWidth = screenWidth
            boardHeight = (screenWidth / origPictureSideRatio).toInt()
        } else {
            boardHeight = screenHeight
            boardWidth = (screenHeight * origPictureSideRatio).toInt()
        }
        val widthMultiple = if (boardWidth > boardHeight)
            prefsHelper.gridDimLong
        else
            prefsHelper.gridDimShort
        val heightMultiple = if (boardWidth > boardHeight)
            prefsHelper.gridDimShort
        else
            prefsHelper.gridDimLong
        boardWidth = boardWidth - boardWidth % widthMultiple
        boardHeight = boardHeight - boardHeight % heightMultiple

        board.setDimensions(boardWidth, boardHeight)
    }

    public override fun onStop() {
        Picasso.with(this).cancelRequest(mTarget)
        super.onStop()
    }

    internal var mTarget: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            adjustBoardDimensions(board, bitmap)
            board.setBitmap(bitmap)
            progressBar.visibility = View.GONE
            board.visibility = View.VISIBLE
            val shake = AnimationUtils.loadAnimation(this@GameActivity, R.anim.shake_anim)
            shuffleBtn.visibility = View.VISIBLE
            shuffleBtn.startAnimation(shake)
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            progressBar.visibility = View.GONE
            toaster.show(R.string.unable_to_load_flickr_picture)
            finish()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }
}
