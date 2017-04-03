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
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.common.Toaster;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;

public class GameActivity extends Activity {

    final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.board)
    PuzzleBoardView mBoard;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.shuffleBtn)
    Button mShuffleBtn;

    int mScreenWidth;
    int mScreenHeight;
    int mBoardWidth;
    int mBoardHeight;

    public String mFileUri;

    @Inject
    PreferencesHelper mPrefsHelper;
    @Inject
    Toaster mToaster;
    @Inject
    FlickrApi mFlickrApi;

    private GameActivityComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent == null) finish();

        setScreenOrientation(getIntent().getExtras().getBoolean(Keys.IS_HORIZONTAL));
        resolveScreenDimensions();

        resolvePictureUri(new Callback() {
            @Override
            public void onFinished() {
                Picasso.with(GameActivity.this)
                        .load(mFileUri)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_STORE)
                        .resize(mScreenWidth, mScreenHeight)
                        .centerInside()
                        .into(mTarget);
            }

            @Override
            public void onError() {
                mToaster.show(R.string.unable_to_load_flickr_picture);
                GameActivity.this.finish();
            }
        });
    }

    void inject() {
        mComponent = DaggerGameActivityComponent.builder()
                .appComponent(((App) getApplication()).getComponent(this))
                //.gameActivityModule(new GameActivityModule(this))
                .build();
        mComponent.inject(this);
    }

    public GameActivityComponent getComponent() {
        return mComponent;
    }

    void resolvePictureUri(Callback callback) {
        mProgressBar.setVisibility(View.VISIBLE);
        mBoard.setVisibility(View.GONE);
        if (getIntent().getExtras() == null) {
            mToaster.show(R.string.picture_not_supplied);
            finish();
        }
        mFileUri = getIntent().getExtras().getString(Keys.PICTURE_URI);
        if (mFileUri != null) {
            callback.onFinished();
        } else {
            String photoStr = getIntent().getExtras().getString(Keys.PHOTO);
            Gson gson = new Gson();
            Photo photo = gson.fromJson(photoStr, Photo.class);
            if (Utils.isOnline(this)) {
                new GetFlickrPhotoSizesTask(this, photo, getMaxScreenDim(), callback).execute();
            } else {
                mToaster.show(R.string.internet_unavailable);
                finish();
            }
        }
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

    int getMaxScreenDim() {
        return (mScreenWidth > mScreenHeight) ? mScreenWidth : mScreenHeight;
    }

    public void shuffle(View v) {
        if (mBoard != null) mBoard.shuffle();
        mShuffleBtn.setVisibility(View.GONE);
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

    interface Callback {
        void onFinished();

        void onError();
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
