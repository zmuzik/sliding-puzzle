package zmuzik.slidingpuzzle2.gamescreen;

import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import retrofit2.Call;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.PhotoSizesResponse;
import zmuzik.slidingpuzzle2.flickr.Size;

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */
class GetFlickrPhotoSizesTask extends AsyncTask<Void, Void, Void> {

    private GameActivity mGameActivity;
    Photo photo;
    List<Size> sizes;
    GameActivity.Callback callback;
    int maxScreenDim;
    String result;

    public GetFlickrPhotoSizesTask(GameActivity gameActivity, Photo photo, int maxScreenDim,
                                   GameActivity.Callback callback) {
        mGameActivity = gameActivity;
        this.photo = photo;
        this.maxScreenDim = maxScreenDim;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String photoId = photo.getId();
        try {
            Call<PhotoSizesResponse> call = mGameActivity.mFlickrApi.getSizes(photoId);
            sizes = call.execute().body().getSizes().getSize();
        } catch (Exception e) {
            result = null;
            Crashlytics.logException(e);
            callback.onError();
        }
        result = photo.getFullPicUrl(maxScreenDim, sizes);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mGameActivity.mFileUri = result;
        callback.onFinished();
    }
}
