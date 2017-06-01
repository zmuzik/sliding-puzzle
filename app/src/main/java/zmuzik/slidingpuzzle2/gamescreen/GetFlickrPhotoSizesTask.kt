package zmuzik.slidingpuzzle2.gamescreen

import android.os.AsyncTask

import com.crashlytics.android.Crashlytics

import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.flickr.Photo
import zmuzik.slidingpuzzle2.flickr.Size

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */
internal class GetFlickrPhotoSizesTask(private val photo: Photo,
                                       private val presenter: GameScreenPresenter,
                                       private val api: FlickrApi) :
        AsyncTask<Void, Void, Void>() {

    private val maxScreenDim: Int = presenter.getMaxScreenDim()
    private var result: String? = null
    private var sizes: List<Size>? = null

    override fun doInBackground(vararg params: Void): Void? {
        val photoId = photo.id
        try {
            val call = api.getSizes(photoId)
            sizes = call.execute().body().sizes.size
        } catch (e: Exception) {
            result = null
            Crashlytics.logException(e)
            presenter.finishWithMessage(R.string.unable_to_load_flickr_picture)
        }

        if (sizes == null) {
            result = null
            presenter.finishWithMessage(R.string.unable_to_load_flickr_picture)
        } else {
            result = photo.getFullPicUrl(maxScreenDim, sizes)
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        presenter.loadPictureUri(result!!)
    }
}
