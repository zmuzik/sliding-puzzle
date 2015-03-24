package zmuzik.slidingpuzzle.ui.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.NewPuzzleBoardView;

public class GameActivity extends Activity {

	final String TAG = this.getClass().getSimpleName();

    int mScreenWidth;
    int mScreenHeight;
    int mBoardWidth;
    int mBoardHeight;

    String mFileUri;
    Bitmap mBitmap;
    Target mTarget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final NewPuzzleBoardView board = (NewPuzzleBoardView) findViewById(R.id.board);
        mFileUri = getIntent().getExtras().getString("FILE_URI");

        resolveScreenDimensions();

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBitmap = bitmap;
                setScreenOrientation(bitmap.getWidth(), bitmap.getHeight());
                resolveScreenDimensions();
                adjustBoardDimensions(board);
                board.setBitmap(mBitmap);
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(this).load(mFileUri).into(mTarget);
	}

    void setScreenOrientation(int bitmapWidth, int bitmapHeight) {
        if (bitmapWidth > bitmapHeight) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    void resolveScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    void adjustBoardDimensions(NewPuzzleBoardView board) {
        float screenSideRatio = (float) mScreenWidth / mScreenHeight;
        float origPictureSideRatio = (float)mBitmap.getWidth() / mBitmap.getHeight();
        if (origPictureSideRatio > screenSideRatio) {
            mBoardWidth = mScreenWidth;
            mBoardHeight = (int) (mScreenWidth / origPictureSideRatio);
        } else {
            mBoardHeight = mScreenHeight;
            mBoardWidth = (int) (mScreenHeight * origPictureSideRatio);
        }
        mBoardWidth = mBoardWidth - (mBoardWidth % 4);
        mBoardHeight = mBoardHeight - (mBoardHeight % 4);

        board.setDimensions(mBoardWidth, mBoardHeight);
    }

    @Override
    public void onStop() {
        Picasso.with(this).cancelRequest(mTarget);
        super.onDestroy();
    }
}
