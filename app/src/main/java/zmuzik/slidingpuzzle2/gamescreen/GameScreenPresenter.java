package zmuzik.slidingpuzzle2.gamescreen;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.common.Toaster;
import zmuzik.slidingpuzzle2.common.di.ActivityContext;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */

@ActivityScope
class GameScreenPresenter {

    private final String TAG = this.getClass().getSimpleName();

    @Inject
    PreferencesHelper mPrefsHelper;
    @Inject
    FlickrApi mFlickrApi;
    @Inject
    Toaster mToaster;
    @Inject
    GameScreenView mView;
    @Inject
    @ActivityContext
    Context mContext;

    public String mPictureUri;

    @Inject
    GameScreenPresenter() {
    }

    void requestPictureUri(Intent intent) {
        String uri = intent.getExtras().getString(Keys.PICTURE_URI);
        if (uri != null) {
            loadPictureUri(uri);
        } else {
            String photoStr = intent.getExtras().getString(Keys.PHOTO);
            Gson gson = new Gson();
            Photo photo = gson.fromJson(photoStr, Photo.class);
            if (Utils.isOnline(mContext)) {
                new GetFlickrPhotoSizesTask(photo, this, mFlickrApi).execute();
            } else {
                mView.finishWithMessage(R.string.internet_unavailable);
            }
        }
    }

    void loadPictureUri(String uri) {
        mPictureUri = uri;
        mView.loadPicture(mPictureUri);
    }

    void finishWithMessage(int stringId) {
        mView.finishWithMessage(stringId);
    }

    int getMaxScreenDim() {
        return mView.getMaxScreenDim();
    }
}
