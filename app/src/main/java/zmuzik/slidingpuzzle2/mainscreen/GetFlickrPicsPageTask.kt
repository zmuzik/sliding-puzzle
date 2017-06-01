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

class GetFlickrPicsPageTask(val mQuery: String, presenter: MainScreenPresenter, api: FlickrApi) :
        AsyncTask<Void, Void, Void>() {
    var resp: SearchResponse? = null
    val presenter: WeakReference<MainScreenPresenter>?
    var mFlickrPhotos: List<Photo>? = null
    val api: WeakReference<FlickrApi>?

    init {
        this.presenter = WeakReference(presenter)
        this.api = WeakReference(api)
    }

    override fun doInBackground(vararg params: Void): Void? {
        if (api == null || api.get() == null) return null
        try {
            val call = api.get()!!.getPhotos(mQuery)
            resp = call.execute().body()
        } catch (e: Exception) {
            resp = null
            Crashlytics.logException(e)
        }

        if (resp != null && resp!!.photos != null) {
            mFlickrPhotos = resp!!.photos.photo
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        if (presenter != null && presenter.get() != null) {
            presenter.get()!!.updateFlickrPictures(mFlickrPhotos)
        }
    }
}
