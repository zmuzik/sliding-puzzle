package zmuzik.slidingpuzzle2.mainscreen

import android.os.AsyncTask
import com.crashlytics.android.Crashlytics
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.flickr.Photo
import zmuzik.slidingpuzzle2.flickr.SearchResponse
import java.lang.ref.WeakReference

/**
 * Created by Zbynek Muzik on 2017-04-01.
 */

class GetFlickrPicsPageTask(val mQuery: String, presenter: MainScreenPresenter, val api: FlickrApi) :
        AsyncTask<Void, Void, Void>() {

    var flickrPhotos: List<Photo>? = null
    val presenterWr = WeakReference(presenter)

    override fun doInBackground(vararg params: Void): Void? {
        try {
            val call = api.getPhotos(mQuery)
            val resp = call.execute().body()
            flickrPhotos = resp?.photos?.photo
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        presenterWr.get()?.updateFlickrPictures(flickrPhotos)
    }
}
