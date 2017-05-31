package zmuzik.slidingpuzzle2.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView : ImageView {

    constructor(context: Context) : super(context) {
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dim = Math.min(measuredHeight, measuredWidth)
        setMeasuredDimension(dim, dim)
    }
}

