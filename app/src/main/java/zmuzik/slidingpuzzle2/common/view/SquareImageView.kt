package zmuzik.slidingpuzzle2.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ImageView(context, attrs, defStyleAttr) {

    init{
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dim = Math.min(measuredHeight, measuredWidth)
        setMeasuredDimension(dim, dim)
    }
}
