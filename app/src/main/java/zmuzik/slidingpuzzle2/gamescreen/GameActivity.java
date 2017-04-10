package zmuzik.slidingpuzzle2.gamescreen;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.common.ShakeDetector;
import zmuzik.slidingpuzzle2.common.Toaster;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;

public class GameActivity extends Activity implements GameScreenView, ShakeDetector.OnShakeListener {

    final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.board)
    PuzzleBoardView mBoard;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.shuffleBtn)
    ImageView mShuffleBtn;

    int mScreenWidth;
    int mScreenHeight;
    int mBoardWidth;
    int mBoardHeight;

    @Inject
    PreferencesHelper mPrefsHelper;
    @Inject
    Toaster mToaster;
    @Inject
    FlickrApi mFlickrApi;
    @Inject
    GameScreenPresenter mPresenter;
    @Inject
    ShakeDetector mShakeDetector;

    private GameActivityComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        mShuffleBtn.startAnimation(shake);
        if (intent == null) {
            mToaster.show(R.string.picture_not_supplied);
            finish();
        }

        setScreenOrientation(getIntent().getExtras().getBoolean(Keys.IS_HORIZONTAL));
        resolveScreenDimensions();

        mProgressBar.setVisibility(View.VISIBLE);
        //mBoard.setVisibility(View.GONE);
        mPresenter.requestPictureUri(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShakeDetector != null) {
            mShakeDetector.register();
            mShakeDetector.setOnShakeListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mShakeDetector != null) {
            mShakeDetector.unRegister();
        }
    }

    @Override
    public void onShake() {
        shuffle(null);
    }

    public void finishWithMessage(int stringId) {
        mToaster.show(stringId);
        finish();
    }

    public void loadPicture(String uri) {
        Picasso.with(this)
                .load(uri)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_STORE)
                .resize(mScreenWidth, mScreenHeight)
                .centerInside()
                .into(mTarget);
    }

    void inject() {
        mComponent = DaggerGameActivityComponent.builder()
                .appComponent(((App) getApplication()).getComponent(this))
                .gameActivityModule(new GameActivityModule(this))
                .build();
        mComponent.inject(this);
        mComponent.inject(mPresenter);
    }

    public GameActivityComponent getComponent() {
        return mComponent;
    }

    void setScreenOrientation(boolean isHorizontal) {
        setRequestedOrientation(isHorizontal
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    void resolveScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    public int getMaxScreenDim() {
        return (mScreenWidth > mScreenHeight) ? mScreenWidth : mScreenHeight;
    }

    public void shuffle(View v) {
        mShuffleBtn.setVisibility(View.GONE);
        if (mBoard != null) mBoard.maybeShuffle();
    }

    void adjustBoardDimensions(PuzzleBoardView board, Bitmap bitmap) {
        float screenSideRatio = (float) mScreenWidth / mScreenHeight;
        float origPictureSideRatio = (float) bitmap.getWidth() / bitmap.getHeight();
        if (origPictureSideRatio > screenSideRatio) {
            mBoardWidth = mScreenWidth;
            mBoardHeight = (int) (mScreenWidth / origPictureSideRatio);
        } else {
            mBoardHeight = mScreenHeight;
            mBoardWidth = (int) (mScreenHeight * origPictureSideRatio);
        }
        int widthMultiple = (mBoardWidth > mBoardHeight)
                ? mPrefsHelper.getGridDimLong()
                : mPrefsHelper.getGridDimShort();
        int heightMultiple = (mBoardWidth > mBoardHeight)
                ? mPrefsHelper.getGridDimShort()
                : mPrefsHelper.getGridDimLong();
        mBoardWidth = mBoardWidth - (mBoardWidth % widthMultiple);
        mBoardHeight = mBoardHeight - (mBoardHeight % heightMultiple);

        board.setDimensions(mBoardWidth, mBoardHeight);
    }

    @Override
    public void onStop() {
        Picasso.with(this).cancelRequest(mTarget);
        super.onStop();
    }

    Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            adjustBoardDimensions(mBoard, bitmap);
            mBoard.setBitmap(bitmap);
            mShuffleBtn.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mBoard.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mProgressBar.setVisibility(View.GONE);
            mToaster.show(R.string.unable_to_load_flickr_picture);
            finish();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
}
