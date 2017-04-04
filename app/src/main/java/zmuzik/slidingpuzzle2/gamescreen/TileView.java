package zmuzik.slidingpuzzle2.gamescreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.ViewGroup;
import android.widget.ImageView;

import zmuzik.slidingpuzzle2.R;

/**
 * Created by Zbynek Muzik on 2017-04-04.
 */

public class TileView extends ImageView {

    private Bitmap mBitmap;
    private int mOrigX;
    private int mOrigY;
    private int mTileNumber;
    private String mNumString;
    private boolean mDisplayNumbers;

    private TextPaint mTextPaint;
    private Rect mBounds;

    public TileView(Context context) {
        super(context);
    }

    public TileView(Context context, int x, int y, Bitmap bitmap, int tileNumber) {
        super(context);
        setImageBitmap(bitmap);
        mOrigX = x;
        mOrigY = y;
        mBitmap = bitmap;
        mTileNumber = tileNumber;
        mBounds = new Rect();
        mNumString = "" + mTileNumber;

        mTextPaint = new TextPaint();
        mTextPaint.getTextBounds(mNumString, 0, mNumString.length(), mBounds);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(getResources().getColor(R.color.white));
        mTextPaint.setShadowLayer(10, 0, 0, getResources().getColor(R.color.black));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setAntiAlias(true);

        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setDisplayNumbers(boolean displayNumbers) {
        mDisplayNumbers = displayNumbers;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDisplayNumbers) {
            int xCoord = getWidth() / 2 - mBounds.width() / 2;
            int yCoord = getHeight() / 2 + mBounds.height() / 2;
            mTextPaint.setTextSize(Math.max(getWidth(), getHeight()) / 4);
            canvas.drawText(mNumString, xCoord, yCoord, mTextPaint);
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getOrigX() {
        return mOrigX;
    }

    public int getOrigY() {
        return mOrigY;
    }

    public int getTileNumber() {
        return mTileNumber;
    }

}
