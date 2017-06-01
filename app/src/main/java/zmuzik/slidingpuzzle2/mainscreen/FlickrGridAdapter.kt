package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import android.view.View
import android.widget.ImageView
import zmuzik.slidingpuzzle2.flickr.Photo
import java.util.*

class FlickrGridAdapter(ctx: Context, var photos: List<Photo>, columns: Int) :
        PicturesGridAdapter(ctx, null, columns) {

    init {
        pictures = ArrayList<OrientedPicture>()
        for (photo in photos) {
            pictures!!.add(OrientedPicture(photo.thumbUrl))
        }
    }

    override fun setOrientationIcon(orientationIcon: ImageView, position: Int) {
        val photo = photos[position]
        orientationIcon.visibility = View.VISIBLE
        val isHorizontal = photo.width_l > photo.height_l
        orientationIcon.rotation = if (isHorizontal) 270f else 0f
    }

    override fun runGame(position: Int) = presenter.runGame(photos[position])
}
