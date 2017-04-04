package zmuzik.slidingpuzzle2.gamescreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Random;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.common.Toaster;

public class PuzzleBoardView extends ViewGroup {

    final String TAG = this.getClass().getSimpleName();

    int mTilesX;
    int mTilesY;
    TileView[][] mTiles;

    Bitmap mCompletePictureBitmap;

    private int mTileWidth;
    private int mTileHeight;

    private int mDownX, mDownY;
    private int mMoveDeltaX, mMoveDeltaY;
    private int mActiveTileX, mActiveTileY;
    private int mBlackTileX, mBlackTileY;
    private boolean mPuzzleComplete;
    private boolean mGameInProgress;
    private Boolean mDisplayNumbers;

    @Inject
    public PreferencesHelper mPrefsHelper;
    @Inject
    public Toaster mToaster;

    public PuzzleBoardView(Context context) {
        super(context);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getContext() instanceof GameActivity) {
            ((GameActivity) getContext()).getComponent().inject(this);
        }
    }

    public void setDimensions(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        params.width = width;
        setLayoutParams(params);
        invalidate();
        int shorterSideTiles = mPrefsHelper.getGridDimLong();
        int longerSideTiles = mPrefsHelper.getGridDimShort();
        mTilesX = width < height ? longerSideTiles : shorterSideTiles;
        mTilesY = width < height ? shorterSideTiles : longerSideTiles;

        mTileWidth = width / mTilesX;
        mTileHeight = height / mTilesY;
    }

    public void setBitmap(Bitmap bitmap) {
        mCompletePictureBitmap = bitmap;

        initTiles();
    }

    void initTiles() {
        Log.d(TAG, "initializing tiles");
        mTiles = new TileView[mTilesX][mTilesY];
        int tileNumber = 1;
        for (int y = 0; y < mTilesY; y++) {
            for (int x = 0; x < mTilesX; x++) {
                Bitmap tileBitmap = Bitmap.createBitmap(mCompletePictureBitmap, x * mTileWidth, y * mTileHeight,
                        mTileWidth, mTileHeight);
                mTiles[x][y] = new TileView(getContext(), x, y, tileBitmap, tileNumber);
                mTiles[x][y].setDisplayNumbers(getDisplayNumbers());
                addView(mTiles[x][y]);
                tileNumber++;
            }
        }
        // sets the black tile to the last tile of the grid
        mBlackTileX = mTilesX - 1;
        mBlackTileY = mTilesY - 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mTiles == null) return;
        for (int i = 0; i < mTilesX; i++) {
            for (int j = 0; j < mTilesY; j++) {
                TileView tile = mTiles[i][j];
                if (mPuzzleComplete || i != mBlackTileX || j != mBlackTileY) {
                    int x = i * mTileWidth;
                    int y = j * mTileHeight;
                    if (isHorizPlayable(i, j)) {
                        x = +mMoveDeltaX;
                    } else if (isVertPlayable(i, j)) {
                        y = +mMoveDeltaY;
                    }
                    tile.layout(x, y, x + mTileWidth, y + mTileHeight);
                }
            }
        }
    }

    private boolean getDisplayNumbers() {
        if (mDisplayNumbers == null) {
            mDisplayNumbers = mPrefsHelper.getDisplayTileNumbers();
        }
        return mDisplayNumbers;
    }

    private boolean isPuzzleComplete() {
        for (int i = 0; i < mTilesX; i++) {
            for (int j = 0; j < mTilesY; j++) {
                TileView t = mTiles[i][j];
                if (t.getOrigX() != i || t.getOrigY() != j) {
                    return false;
                }
            }
        }
        Log.d(TAG, "puzzle complete");
        mToaster.show(R.string.congrats);
        return true;
    }

    public boolean isHorizPlayable(int x, int y) {
        return y == mBlackTileY && y == mActiveTileY &&
                ((mActiveTileX <= x && x < mBlackTileX) || (mBlackTileX < x && x <= mActiveTileX));
    }

    public boolean isVertPlayable(int x, int y) {
        return x == mBlackTileX && x == mActiveTileX &&
                ((mActiveTileY <= y && y < mBlackTileY) || (mBlackTileY < y && y <= mActiveTileY));
    }

    public void shuffle() {
        Random random = new Random();
        int position;
        int steps = mTilesX * mTilesY * 4;
        for (int step = 0; step < steps; step++) {
            if ((step % 2) == 1) {
                position = random.nextInt(mTilesX - 1);
                if (position >= mBlackTileX) position++;
                playTile(position, mBlackTileY);
            } else {
                position = random.nextInt(mTilesY - 1);
                if (position >= mBlackTileY) position++;
                playTile(mBlackTileX, position);
            }
            invalidate();
        }
        mGameInProgress = true;
    }

    public void playTile(int x, int y) {
        TileView temp = mTiles[mBlackTileX][mBlackTileY];
        if (x == mBlackTileX) {
            if (y < mBlackTileY) {
                for (int i = mBlackTileY - 1; i >= y; i--) {
                    mTiles[mBlackTileX][i + 1] = mTiles[mBlackTileX][i];
                }
            } else if (y > mBlackTileY) {
                for (int i = mBlackTileY + 1; i <= y; i++) {
                    mTiles[mBlackTileX][i - 1] = mTiles[mBlackTileX][i];
                }
            }
        } else if (y == mBlackTileY) {
            if (x < mBlackTileX) {
                for (int i = mBlackTileX - 1; i >= x; i--) {
                    mTiles[i + 1][mBlackTileY] = mTiles[i][mBlackTileY];
                }
            } else if (x > mBlackTileX) {
                for (int i = mBlackTileX + 1; i <= x; i++) {
                    mTiles[i - 1][mBlackTileY] = mTiles[i][mBlackTileY];
                }
            }
        }
        mTiles[x][y] = temp;
        mBlackTileX = x;
        mBlackTileY = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mGameInProgress || event == null) return true;

        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (!mPuzzleComplete) {
                mDownX = eventX;
                mDownY = eventY;
                mActiveTileX = mDownX / mTileWidth;
                mActiveTileY = mDownY / mTileHeight;
            }
        }

        if (action == MotionEvent.ACTION_MOVE) {
            if (mActiveTileX == mBlackTileX) {
                mMoveDeltaY = eventY - mDownY;
                mMoveDeltaX = 0;
                if (mActiveTileY < mBlackTileY) {
                    mMoveDeltaY = (mMoveDeltaY > mTileHeight) ? mTileHeight : mMoveDeltaY;
                    mMoveDeltaY = (mMoveDeltaY < 0) ? 0 : mMoveDeltaY;
                } else if (mActiveTileY > mBlackTileY) {
                    mMoveDeltaY = (mMoveDeltaY < -mTileHeight) ? -mTileHeight : mMoveDeltaY;
                    mMoveDeltaY = (mMoveDeltaY > 0) ? 0 : mMoveDeltaY;
                }
            } else if (mActiveTileY == mBlackTileY) {
                mMoveDeltaX = eventX - mDownX;
                mMoveDeltaY = 0;
                if (mActiveTileX < mBlackTileX) {
                    mMoveDeltaX = (mMoveDeltaX > mTileWidth) ? mTileWidth : mMoveDeltaX;
                    mMoveDeltaX = (mMoveDeltaX < 0) ? 0 : mMoveDeltaX;
                } else if (mActiveTileX > mBlackTileX) {
                    mMoveDeltaX = (mMoveDeltaX < -mTileWidth) ? -mTileWidth : mMoveDeltaX;
                    mMoveDeltaX = (mMoveDeltaX > 0) ? 0 : mMoveDeltaX;
                }
            }
            invalidate();
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (Math.abs(mMoveDeltaX) > mTileWidth / 2 || Math.abs(mMoveDeltaY) > mTileHeight / 2) {
                playTile(mActiveTileX, mActiveTileY);
                mPuzzleComplete = isPuzzleComplete();
            }
            mMoveDeltaX = 0;
            mMoveDeltaY = 0;
            invalidate();
        }
        return true;
    }
}