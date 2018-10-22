package zmuzik.slidingpuzzle2.screens.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.ViewGroup
import android.widget.ImageView

import zmuzik.slidingpuzzle2.R


class TileView : ImageView {

    lateinit var bitmap: Bitmap
    var origX: Int = 0
    var origY: Int = 0
    var tileWidth: Int = 0
    var tileHeight: Int = 0
    var tileNumber: Int = 0
    lateinit var numString: String
    var displayNumbers: Boolean = false

    lateinit var textPaint: TextPaint
    lateinit var bounds: Rect

    constructor(context: Context) : super(context)

    constructor(context: Context, x: Int, y: Int, bitmap: Bitmap, tileNumber: Int) : super(context) {
        setImageBitmap(bitmap)
        this.bitmap = bitmap
        tileWidth = this.bitmap.width
        tileHeight = this.bitmap.height
        origX = x
        origY = y
        this.tileNumber = tileNumber
        bounds = Rect()
        numString = "" + this.tileNumber

        textPaint = TextPaint()
        textPaint.textSize = (Math.max(tileWidth, tileHeight) / 4).toFloat()
        textPaint.color = resources.getColor(R.color.white)
        textPaint.setShadowLayer(10f, 0f, 0f, resources.getColor(R.color.black))
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 3f
        textPaint.isAntiAlias = true
        textPaint.getTextBounds(numString, 0, numString.length, bounds)

        layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(bitmap.width, bitmap.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (displayNumbers) {
            val xCoord = tileWidth / 2 - bounds.width() / 2
            val yCoord = tileHeight / 2 + bounds.height() / 2
            canvas.drawText(numString, xCoord.toFloat(), yCoord.toFloat(), textPaint)
        }
    }
}
