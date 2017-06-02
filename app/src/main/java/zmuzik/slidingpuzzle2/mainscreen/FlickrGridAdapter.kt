package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import android.view.View
import android.widget.ImageView
import zmuzik.slidingpuzzle2.flickr.Photo

class FlickrGridAdapter(ctx: Context, var photos: List<Photo>, columns: Int) :
        PicturesGridAdapter(ctx, mutableListOf<String>(), columns) {

    init {
        pictures = photos.map { OrientedPicture(it.thumbUrl) }
    }

    override fun setOrientationIcon(orientationIcon: ImageView, position: Int) {
        val photo = photos[position]
        val isHorizontal = photo.width_l > photo.height_l
        orientationIcon.visibility = View.VISIBLE
        orientationIcon.rotation = if (isHorizontal) 270f else 0f
    }

    override fun runGame(position: Int) = presenter.runGame(photos[position])
}
