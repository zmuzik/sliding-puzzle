package zmuzik.slidingpuzzle2.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.adapters.FlickrGridAdapter;
import zmuzik.slidingpuzzle2.adapters.PicturesGridAdapter;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.PhotoSizesResponse;
import zmuzik.slidingpuzzle2.flickr.Size;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;
import zmuzik.slidingpuzzle2.view.PuzzleBoardView;

public class GameActivity extends Activity {

    final String TAG = this.getClass().getSimpleName();

    PuzzleBoardView board;
    ProgressBar progressBar;
    Button shuffleBtn;

    int mScreenWidth;
    int mScreenHeight;
    int mBoardWidth;
    int mBoardHeight;

    public String mFileUri;

    @Inject
    PrefsHelper mPrefsHelper;

    @Inject
    FlickrApi mFlickrApi;

    Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            adjustBoardDimensions(board, bitmap);
            board.setBitmap(bitmap);
            shuffleBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            board.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(App.get(), App.get().getString(R.string.unable_to_load_flickr_picture), Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent(this).inject(this);
        Intent intent = getIntent();
        if (intent == null) finish();
        boolean isHorizontal = getIntent().getExtras().getBoolean(PicturesGridAdapter.IS_HORIZONTAL);
        setScreenOrientation(isHorizontal);
        resolveScreenDimensions();
        setContentView(R.layout.activity_game);
        board = (PuzzleBoardView) findViewById(R.id.board);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        shuffleBtn = (Button) findViewById(R.id.shuffleBtn);

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
                Toast.makeText(App.get(), App.get().getString(R.string.unable_to_load_flickr_picture), Toast.LENGTH_LONG).show();
                GameActivity.this.finish();
            }
        });
    }

    void resolvePictureUri(Callback callback) {
        progressBar.setVisibility(View.VISIBLE);
        board.setVisibility(View.GONE);
        if (getIntent().getExtras() == null) {
            Toast.makeText(App.get(), App.get().getString(R.string.picture_not_supplied), Toast.LENGTH_LONG).show();
            finish();
        }
        mFileUri = getIntent().getExtras().getString(PicturesGridAdapter.FILE_URI);
        if (mFileUri != null) {
            callback.onFinished();
        } else {
            String photoStr = getIntent().getExtras().getString(FlickrGridAdapter.PHOTO);
            Gson gson = new Gson();
            Photo photo = gson.fromJson(photoStr, Photo.class);
            if (Utils.isOnline(this)) {
                new GetFlickrPhotoSizesTask(photo, getMaxScreenDim(), callback).execute();
            } else {
                Toast.makeText(App.get(), App.get().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    void setScreenOrientation(int bitmapWidth, int bitmapHeight) {
        if (bitmapWidth > bitmapHeight) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        if (board != null) board.shuffle();
        shuffleBtn.setVisibility(View.GONE);
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

    private class GetFlickrPhotoSizesTask extends AsyncTask<Void, Void, Void> {

        Photo photo;
        List<Size> sizes;
        Callback callback;
        int maxScreenDim;
        String result;

        public GetFlickrPhotoSizesTask(Photo photo, int maxScreenDim, Callback callback) {
            this.photo = photo;
            this.maxScreenDim = maxScreenDim;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String photoId = photo.getId();
            try {
                Call<PhotoSizesResponse> call = mFlickrApi.getSizes(photoId);
                sizes = call.execute().body().getSizes().getSize();
            } catch (Exception e) {
                result = null;
                Crashlytics.logException(e);
                callback.onError();
            }
            result = photo.getFullPicUrl(maxScreenDim, sizes);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFileUri = result;
            callback.onFinished();
        }
    }

    private interface Callback {
        void onFinished();

        void onError();
    }
}
