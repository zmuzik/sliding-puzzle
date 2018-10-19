package zmuzik.slidingpuzzle2.screens.home

import android.content.Context
import android.util.AttributeSet
import zmuzik.slidingpuzzle2.common.PictureTab


class FlickrPicturesView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BasePicturesView(context, attrs, defStyleAttr) {

    override val tab = PictureTab.FLICKR
}
