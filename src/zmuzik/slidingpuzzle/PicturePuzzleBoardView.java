package zmuzik.slidingpuzzle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class PicturePuzzleBoardView extends View {

	final String TAG = this.getClass().getSimpleName();
	int mGridDimX;
	int mGridDimY;
	Tile[][] tiles;
	int mMainPicture = 0;

	double picDimRatio;
	TextPaint mTextPaint;
	Rect mBounds;

	Bitmap mCompletePictureBitmap;
	Bitmap mOriginalBitmap;
	boolean mTouching = false;
	boolean mIsViewDimAdjusted = false;

	private Paint mPaint;
	private int mViewWidth;
	private int mViewHeight;
	private int mTileWidth;
	private int mTileHeight;

	private int mDownX, mDownY;
	private int mMoveDeltaX, mMoveDeltaY;
	private int mActiveTileX, mActiveTileY;
	private int mBlackTileX, mBlackTileY;
	private boolean puzzleComplete;

	public PicturePuzzleBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public PicturePuzzleBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PicturePuzzleBoardView(Context context) {
		super(context);
		init(context);
	}

	public PicturePuzzleBoardView(Context context, Intent intent) {
		super(context);
		Bundle params = intent.getExtras();
		mGridDimX = params.getInt("gridSizeLonger");
		mGridDimY = params.getInt("gridSizeShorter");
		mMainPicture = params.getInt("picture");
		init(context);
	}

	void init(Context context) {
		Log.d(TAG, "init method started");

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

		try {
			String mainPictureFileName = "game_pic_" + mMainPicture + ".jpg";
			InputStream ims = context.getAssets().open(mainPictureFileName);
			Drawable drawable = Drawable.createFromStream(ims, null);
			mOriginalBitmap = ((BitmapDrawable) drawable).getBitmap();

			int x = mOriginalBitmap.getWidth();
			int y = mOriginalBitmap.getHeight();
			picDimRatio = x / y;

			// set orientation according to the picture
			Activity activity = (Activity) context;
			int origOrientation = activity.getResources().getConfiguration().orientation;
			if (x > y && origOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else if (x < y && origOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} catch (IOException e) {
			Log.e(TAG, "init: ", e);
		}
	}

	void initTiles() {
		Log.d(TAG, "initializing tiles");
		tiles = new Tile[mGridDimX][mGridDimY];
		int tileNumber = 1;
		for (int y = 0; y < mGridDimY; y++) {
			for (int x = 0; x < mGridDimX; x++) {
				Bitmap bitmap = Bitmap.createBitmap(mCompletePictureBitmap, x * mTileWidth, y * mTileHeight,
						mTileWidth, mTileHeight);
				tiles[x][y] = new Tile(x, y, bitmap, tileNumber);
				tileNumber++;
			}
		}
		// sets the black tile to the last tile of the grid
		mBlackTileX = mGridDimX - 1;
		mBlackTileY = mGridDimY - 1;
		// save the tiles' starting position
		mCompletePictureBitmap.recycle();
		mCompletePictureBitmap = null;
		shuffle();
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
		Log.d(TAG, "onSizeChanged: width: " + width + " height: " + height);
		Log.d(TAG, "picture : width: " + mOriginalBitmap.getWidth() + " height: " + mOriginalBitmap.getHeight());

		float pictureSideRatio = (float) mOriginalBitmap.getWidth() / mOriginalBitmap.getHeight();
		float viewSideRatio = (float) width / height;
		Log.d(TAG, "pictureSideRatio: " + pictureSideRatio + " viewSideRatio: " + viewSideRatio);

		if (!mIsViewDimAdjusted) {
			if (pictureSideRatio > viewSideRatio) {
				// obrazek je sirsi. sirka obrazku bude sirka view a vyska
				// obrazku se dopocita
				mViewWidth = width;
				mViewHeight = (int) (width / pictureSideRatio);
			} else {
				// obrazek je vyssi. vyska obrazku bude vyska view a sirka
				// obrazku se dopocita
				mViewHeight = height;
				mViewWidth = (int) (height * pictureSideRatio);
			}
			mViewWidth = mViewWidth - (mViewWidth % mGridDimX);
			mViewHeight = mViewHeight - (mViewHeight % mGridDimY);
			mIsViewDimAdjusted = true;
		}

		Log.d(TAG, "mViewWidth: " + mViewWidth + " mViewHeight: " + mViewHeight);

		if (width != mViewWidth || height != mViewHeight) {
			Log.d(TAG, "Fixing view dimensions. Values now: " + width + " " + height + " Supposed values: "
					+ mViewWidth + " " + mViewHeight);
			LayoutParams params = getLayoutParams();
			params.height = mViewHeight;
			params.width = mViewWidth;
			setLayoutParams(params);
			invalidate();
			return;
		}

		mTileWidth = width / mGridDimX;
		mTileHeight = height / mGridDimY;

		mCompletePictureBitmap = Bitmap.createScaledBitmap(mOriginalBitmap, mViewWidth, mViewHeight, true);
		Log.d(TAG, "disposing original bitmap");
		initTiles();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (tiles == null) {
			return;
		}
		// draw all tiles - initialization
		int tileNumber = 0;
		for (int i = 0; i < mGridDimX; i++) {
			for (int j = 0; j < mGridDimY; j++) {
				Tile t = tiles[i][j];
				tileNumber = t.getTileNumber();
				if (puzzleComplete || i != mBlackTileX || j != mBlackTileY) {
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
		if (!puzzleComplete) {
			String numStr = "" + number;
			mTextPaint.getTextBounds(numStr, 0, numStr.length(), mBounds);
			int xCoord = tileX * mTileWidth + addX + (mTileWidth / 2 - mBounds.width() / 2);
			int yCoord = tileY * mTileHeight + addY + (mTileHeight / 2 + mBounds.height() / 2);
			canvas.drawText(numStr, xCoord, yCoord, mTextPaint);
		}
	}

	private boolean isPuzzleComplete() {
		for (int i = 0; i < mGridDimX; i++) {
			for (int j = 0; j < mGridDimY; j++) {
				Tile t = tiles[i][j];
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
		int steps = mGridDimX * mGridDimY * 2;
		for (int i = 0; i < steps; i++) {
			if ((i % 2) == 1) {
				position = random.nextInt(mGridDimX - 1);
				if (position >= mBlackTileX)
					position++;
				Log.d(TAG, "shuffle play " + position + " " + mBlackTileY);
				playTile(position, mBlackTileY);
			} else {
				position = random.nextInt(mGridDimY - 1);
				if (position >= mBlackTileY)
					position++;
				Log.d(TAG, "shuffle play " + mBlackTileX + " " + position);
				playTile(mBlackTileX, position);
			}
		}
	}

	public int playTile(int x, int y) {
		int totalMovedTiles = 0;
		Tile temp = tiles[mBlackTileX][mBlackTileY];
		Log.d(TAG, "black: " + mBlackTileX + " " + mBlackTileY);
		Log.d(TAG, "active: " + mActiveTileX + " " + mActiveTileY);
		Log.d(TAG, "xy: " + x + " " + y);
		if (x == mBlackTileX) {
			if (y < mBlackTileY) {
				for (int i = mBlackTileY - 1; i >= y; i--) {
					tiles[mBlackTileX][i + 1] = tiles[mBlackTileX][i];
					totalMovedTiles++;
					Log.d(TAG, mBlackTileX + " " + (i + 1) + " -> " + mBlackTileX + " " + i);
				}
			} else if (y > mBlackTileY) {
				for (int i = mBlackTileY + 1; i <= y; i++) {
					tiles[mBlackTileX][i - 1] = tiles[mBlackTileX][i];
					totalMovedTiles++;
				}
			}
		} else if (y == mBlackTileY) {
			if (x < mBlackTileX) {
				for (int i = mBlackTileX - 1; i >= x; i--) {
					tiles[i + 1][mBlackTileY] = tiles[i][mBlackTileY];
					totalMovedTiles++;
				}
			} else if (x > mBlackTileX) {
				for (int i = mBlackTileX + 1; i <= x; i++) {
					tiles[i - 1][mBlackTileY] = tiles[i][mBlackTileY];
					totalMovedTiles++;
				}
			}
		}
		tiles[x][y] = temp;
		mBlackTileX = x;
		mBlackTileY = y;
		Log.d(TAG, "total moved tiles " + totalMovedTiles);
		return totalMovedTiles;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventX = (int) event.getX();
		int eventY = (int) event.getY();
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			mTouching = false;
			if (Math.abs(mMoveDeltaX) > mTileWidth / 2 || Math.abs(mMoveDeltaY) > mTileHeight / 2) {
				playTile(mActiveTileX, mActiveTileY);
				if (isPuzzleComplete()) {
					puzzleComplete = true;
				}
			}

			mDownX = -1;
			mDownY = -1;
			mActiveTileX = -1;
			mActiveTileY = -1;
			mMoveDeltaX = 0;
			mMoveDeltaY = 0;
			invalidate();
		}
		if (action == MotionEvent.ACTION_DOWN) {
			if (!puzzleComplete) {
				mTouching = true;
				mDownX = eventX;
				mDownY = eventY;
				mActiveTileX = mDownX / mTileWidth;
				mActiveTileY = mDownY / mTileHeight;
				// Log.d(TAG, "x=" + mDownX + "y=" + mDownY + "tilex=" +
				// mActiveTileX + "tiley=" + mActiveTileY);
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
			// Log.d(TAG, "deltax: " +mMoveDeltaX + " deltay: "+ mMoveDeltaY);
			invalidate();
		}
		return true;
	}
}