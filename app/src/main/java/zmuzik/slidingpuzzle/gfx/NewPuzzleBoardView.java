package zmuzik.slidingpuzzle.gfx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;

import zmuzik.slidingpuzzle.helpers.PrefsHelper;
import zmuzik.slidingpuzzle.model.Tile;

public class NewPuzzleBoardView extends View {

    final String TAG = this.getClass().getSimpleName();
    int mTilesX = PrefsHelper.get().getGridDimLong();
    int mTilesY = PrefsHelper.get().getGridDimShort();
    Tile[][] mTiles;
    Context mContext;

    TextPaint mTextPaint;
    Rect mBounds;

    Bitmap mCompletePictureBitmap;

    private Paint mPaint;
    private int mViewWidth;
    private int mViewHeight;
    private int mTileWidth;
    private int mTileHeight;

    private int mDownX, mDownY;
    private int mMoveDeltaX, mMoveDeltaY;
    private int mActiveTileX, mActiveTileY;
    private int mBlackTileX, mBlackTileY;
    private boolean mPuzzleComplete;

    public NewPuzzleBoardView(Context context) {
        super(context);
        mContext = context;
        initPaints();
    }

    public NewPuzzleBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPaints();
    }

    public NewPuzzleBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaints();
    }

    public void initPaints() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(0);
        mPaint.setFilterBitmap(false);

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setShadowLayer(10, 0, 0, 0xff000000);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(3);
        mBounds = new Rect();
    }

    public void setDimensions(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        params.width = width;
        setLayoutParams(params);
        invalidate();

        mTileWidth = width / mTilesX;
        mTileHeight = height / mTilesY;
        mViewWidth = width;
        mViewHeight = height;
    }

    public void setBitmap(Bitmap bitmap) {
        mCompletePictureBitmap = Bitmap.createScaledBitmap(bitmap, mViewWidth, mViewHeight, true);
        initTiles();
    }

    void initTiles() {
        Log.d(TAG, "initializing tiles");
        mTiles = new Tile[mTilesX][mTilesY];
        int tileNumber = 1;
        for (int y = 0; y < mTilesY; y++) {
            for (int x = 0; x < mTilesX; x++) {
                Bitmap bitmap = Bitmap.createBitmap(mCompletePictureBitmap, x * mTileWidth, y * mTileHeight,
                        mTileWidth, mTileHeight);
                mTiles[x][y] = new Tile(x, y, bitmap, tileNumber);
                tileNumber++;
            }
        }
        // sets the black tile to the last tile of the grid
        mBlackTileX = mTilesX - 1;
        mBlackTileY = mTilesY - 1;
        // save the tiles' starting position
        mCompletePictureBitmap.recycle();
        mCompletePictureBitmap = null;
        shuffle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTiles == null) {
            return;
        }

        int tileNumber = 0;
        for (int i = 0; i < mTilesX; i++) {
            for (int j = 0; j < mTilesY; j++) {
                Tile t = mTiles[i][j];
                tileNumber = t.getTileNumber();
                if (mPuzzleComplete || i != mBlackTileX || j != mBlackTileY) {
                    // anything but the black tile
                    Bitmap bitmap = t.getBitmap();
                    if (isHorizPlayable(i, j)) {
                        canvas.drawBitmap(bitmap, i * mTileWidth + mMoveDeltaX, j * mTileHeight, mPaint);
                        drawNumberOnTile(canvas, tileNumber, i, j, mMoveDeltaX, 0);
                    } else if (isVertPlayable(i, j)) {
                        canvas.drawBitmap(bitmap, i * mTileWidth, j * mTileHeight + mMoveDeltaY, mPaint);
                        drawNumberOnTile(canvas, tileNumber, i, j, 0, mMoveDeltaY);
                    } else {
                        // rest of the tiles
                        canvas.drawBitmap(bitmap, i * mTileWidth, j * mTileHeight, mPaint);
                        drawNumberOnTile(canvas, tileNumber, i, j, 0, 0);
                    }
                }
            }
        }
    }

    void drawNumberOnTile(Canvas canvas, int number, int tileX, int tileY, int addX, int addY) {
        if (!mPuzzleComplete) {
            String numStr = "" + number;
            mTextPaint.getTextBounds(numStr, 0, numStr.length(), mBounds);
            int xCoord = tileX * mTileWidth + addX + (mTileWidth / 2 - mBounds.width() / 2);
            int yCoord = tileY * mTileHeight + addY + (mTileHeight / 2 + mBounds.height() / 2);
            canvas.drawText(numStr, xCoord, yCoord, mTextPaint);
        }
    }

    private boolean isPuzzleComplete() {
        for (int i = 0; i < mTilesX; i++) {
            for (int j = 0; j < mTilesY; j++) {
                Tile t = mTiles[i][j];
                if (t.getOrigX() != i || t.getOrigY() != j) {
                    Log.d(TAG, "not complete: " + i + j);
                    return false;
                }
            }
        }
        Log.d(TAG, "puzzle complete");
        Toast.makeText(getContext(), "Congratulations!", Toast.LENGTH_SHORT).show();
        return true;
    }

    public boolean isHorizPlayable(int x, int y) {
        return y == mBlackTileY && y == mActiveTileY
                && ((mActiveTileX <= x && x < mBlackTileX) || (mBlackTileX < x && x <= mActiveTileX));
    }

    public boolean isVertPlayable(int x, int y) {
        return x == mBlackTileX && x == mActiveTileX
                && ((mActiveTileY <= y && y < mBlackTileY) || (mBlackTileY < y && y <= mActiveTileY));
    }

    public void shuffle() {
        Random random = new Random();
        int position;
        int steps = mTilesX * mTilesY * 2;
        for (int i = 0; i < steps; i++) {
            if ((i % 2) == 1) {
                position = random.nextInt(mTilesX - 1);
                if (position >= mBlackTileX)
                    position++;
                Log.d(TAG, "shuffle play " + position + " " + mBlackTileY);
                playTile(position, mBlackTileY);
            } else {
                position = random.nextInt(mTilesY - 1);
                if (position >= mBlackTileY)
                    position++;
                Log.d(TAG, "shuffle play " + mBlackTileX + " " + position);
                playTile(mBlackTileX, position);
            }
        }
    }

    public int playTile(int x, int y) {
        int totalMovedTiles = 0;
        Tile temp = mTiles[mBlackTileX][mBlackTileY];
        if (x == mBlackTileX) {
            if (y < mBlackTileY) {
                for (int i = mBlackTileY - 1; i >= y; i--) {
                    mTiles[mBlackTileX][i + 1] = mTiles[mBlackTileX][i];
                    totalMovedTiles++;
                    Log.d(TAG, mBlackTileX + " " + (i + 1) + " -> " + mBlackTileX + " " + i);
                }
            } else if (y > mBlackTileY) {
                for (int i = mBlackTileY + 1; i <= y; i++) {
                    mTiles[mBlackTileX][i - 1] = mTiles[mBlackTileX][i];
                    totalMovedTiles++;
                }
            }
        } else if (y == mBlackTileY) {
            if (x < mBlackTileX) {
                for (int i = mBlackTileX - 1; i >= x; i--) {
                    mTiles[i + 1][mBlackTileY] = mTiles[i][mBlackTileY];
                    totalMovedTiles++;
                }
            } else if (x > mBlackTileX) {
                for (int i = mBlackTileX + 1; i <= x; i++) {
                    mTiles[i - 1][mBlackTileY] = mTiles[i][mBlackTileY];
                    totalMovedTiles++;
                }
            }
        }
        mTiles[x][y] = temp;
        mBlackTileX = x;
        mBlackTileY = y;
        return totalMovedTiles;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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