package zmuzik.slidingpuzzle2.gamescreen;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import retrofit2.Call;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.PhotoSizesResponse;
import zmuzik.slidingpuzzle2.flickr.Size;

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */
class GetFlickrPhotoSizesTask extends AsyncTask<Void, Void, Void> {

    private final FlickrApi mApi;
    private final Photo mPhoto;
    private final int mMaxScreenDim;
    private final GameScreenPresenter mPresenter;
    private String mResult;
    private List<Size> mSizes;

    public GetFlickrPhotoSizesTask(Photo photo, GameScreenPresenter presenter, FlickrApi api) {
        mPhoto = photo;
        mPresenter = presenter;
        mApi = api;
        mMaxScreenDim = mPresenter.getMaxScreenDim();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String photoId = mPhoto.getId();
        try {
            Call<PhotoSizesResponse> call = mApi.getSizes(photoId);
            mSizes = call.execute().body().getSizes().getSize();
        } catch (Exception e) {
            mResult = null;
            Crashlytics.logException(e);
            mPresenter.finishWithMessage(R.string.unable_to_load_flickr_picture);
        }
        mResult = mPhoto.getFullPicUrl(mMaxScreenDim, mSizes);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mPresenter.loadPictureUri(mResult);
    }
}
