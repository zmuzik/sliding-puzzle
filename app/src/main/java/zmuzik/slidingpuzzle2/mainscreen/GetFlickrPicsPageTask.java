package zmuzik.slidingpuzzle2.mainscreen;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.common.Toaster;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.SearchResponse;

/**
 * Created by Zbynek Muzik on 2017-04-01.
 */

public class GetFlickrPicsPageTask extends AsyncTask<Void, Void, Void> {

    private String mQuery;
    private SearchResponse resp;
    private WeakReference<MainScreenPresenter> mPresenter;
    private List<Photo> mFlickrPhotos;
    private WeakReference<FlickrApi> mApi;

    public GetFlickrPicsPageTask(String query, MainScreenPresenter presenter, FlickrApi api) {
        mQuery = query;
        mPresenter = new WeakReference<MainScreenPresenter>(presenter);
        mApi = new WeakReference<FlickrApi>(api);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mApi == null || mApi.get() == null) return null;
        try {
            Call<SearchResponse> call = mApi.get().getPhotos(mQuery);
            resp = call.execute().body();
        } catch (Exception e) {
            resp = null;
            Crashlytics.logException(e);
        }
        if (resp != null && resp.getPhotos() != null) {
            mFlickrPhotos = resp.getPhotos().getPhoto();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (resp == null) {
            Toaster.show(R.string.err_querying_flickr);
        } else if (mPresenter != null && mPresenter.get() != null) {
            mPresenter.get().updateFlickrPictures(mFlickrPhotos);
        }
    }
}
