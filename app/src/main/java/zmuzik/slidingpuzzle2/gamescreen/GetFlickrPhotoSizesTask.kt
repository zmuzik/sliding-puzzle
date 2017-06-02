package zmuzik.slidingpuzzle2.gamescreen

import android.os.AsyncTask

import com.crashlytics.android.Crashlytics

import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.flickr.Photo
import zmuzik.slidingpuzzle2.flickr.Size
import java.lang.ref.WeakReference

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */
internal class GetFlickrPhotoSizesTask(private val photo: Photo,
                                       private val presenter: GameScreenPresenter,
                                       private val api: FlickrApi) :
        AsyncTask<Void, Void, Void>() {

    val presenterWr = WeakReference(presenter)
    val maxScreenDim: Int = presenter.getMaxScreenDim()
    var sizes: List<Size>? = null
    var result: String? = null

    override fun doInBackground(vararg params: Void): Void? {
        try {
            val call = api.getSizes(photo.id)
            sizes = call.execute().body().sizes?.size
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }

        sizes?.let { result = photo.getFullPicUrl(maxScreenDim, it) }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        result?.let {
            presenterWr.get()?.loadPictureUri(it)
            return
        }
        presenterWr.get()?.finishWithMessage(R.string.unable_to_load_flickr_picture)
    }
}
