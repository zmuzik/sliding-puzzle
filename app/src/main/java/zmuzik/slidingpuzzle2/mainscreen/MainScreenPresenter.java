package zmuzik.slidingpuzzle2.mainscreen;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

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
import zmuzik.slidingpuzzle2.gamescreen.GameActivity;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
public class MainScreenPresenter {

    private final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_PERMISSION_READ_STORAGE = 101;

    private final String[] SAVED_PICTURES = {
            Utils.ASSET_PREFIX + "game_pic_00.jpg",
            Utils.ASSET_PREFIX + "game_pic_01.jpg",
            Utils.ASSET_PREFIX + "game_pic_07.jpg",
            Utils.ASSET_PREFIX + "game_pic_02.jpg",
            Utils.ASSET_PREFIX + "game_pic_03.jpg",
            Utils.ASSET_PREFIX + "game_pic_04.jpg",
            Utils.ASSET_PREFIX + "game_pic_05.jpg",
            Utils.ASSET_PREFIX + "game_pic_06.jpg",
            Utils.ASSET_PREFIX + "game_pic_08.jpg",
            Utils.ASSET_PREFIX + "game_pic_09.jpg",
            Utils.ASSET_PREFIX + "game_pic_10.jpg",
            Utils.ASSET_PREFIX + "game_pic_11.jpg"};

    @Inject
    PreferencesHelper mPrefsHelper;
    @Inject
    FlickrApi mFlickrApi;
    @Inject
    Toaster mToaster;
    @Inject
    MainScreenView mView;

    @Inject
    @ActivityContext
    Context mContext;

    private boolean isCameraPicturesUpdating;
    private List<Photo> mFlickerPictures;

    @Inject
    public MainScreenPresenter() {
    }

    void onResume() {
        requestUpdateCameraPictures();
    }

    void onPause() {
    }

    boolean toggleShowNumbers() {
        boolean onOff = mPrefsHelper.getDisplayTileNumbers();
        onOff = !onOff;
        mPrefsHelper.setDisplayTileNumbers(onOff);
        return onOff;
    }

    String getGridDimensions() {
        return mPrefsHelper.getGridDimShort() + "x" + mPrefsHelper.getGridDimLong();
    }

    void setGridDimensions(int gridDimShort, int gridDimLong) {
        mPrefsHelper.setGridDimShort(gridDimShort);
        mPrefsHelper.setGridDimLong(gridDimLong);
    }

    void runGame(String pictureUri, boolean isHorizontal) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(Keys.PICTURE_URI, pictureUri);
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
    }

    void runGame(Photo photo) {
        boolean isHorizontal = photo != null && photo.getWidth_l() > photo.getHeight_l();
        Intent intent = new Intent(mContext, GameActivity.class);
        String photoStr = new Gson().toJson(photo);
        intent.putExtra(Keys.PHOTO, photoStr);
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
    }

    //***SavedPicturesGridView***

    void requestUpdateSavedPictures() {
        updateSavedPictures();
    }

    void updateSavedPictures() {
        mView.updateSavedPictures(Arrays.asList(SAVED_PICTURES));
    }

    //***CameraPicturesGridView***

    void requestUpdateCameraPictures() {
        if (!isReadExternalGranted() && mPrefsHelper.shouldAskReadStoragePerm()) {
            requestReadExternalPermission();
        } else if (!isCameraPicturesUpdating) {
            isCameraPicturesUpdating = true;
            new UpdateCameraFilesTask(this).execute();
        }
    }

    void updateCameraPictures(List<String> pictures) {
        isCameraPicturesUpdating = false;
        mView.updateCameraPictures(pictures);
    }

    boolean isReadExternalGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    void requestReadExternalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MainActivity) mContext).
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            mPrefsHelper.setShouldAskReadStoragePerm(false);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUpdateCameraPictures();
            }
        }
    }

    public void launchCameraApp() {
        Intent auxIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = mContext.getPackageManager();
            ResolveInfo mInfo = pm.resolveActivity(auxIntent, 0);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.i(TAG, "Unable to launch camera: " + e);
        }
    }

    //***FlickrPicturesGridView***

    public void requestFlickrSearch(String keywords) {
        if (!Utils.isOnline(mContext)) {
            mToaster.show(R.string.internet_unavailable);
            return;
        }

        if (keywords == null || "".equals(keywords)) {
            mToaster.show(R.string.keyword_not_supplied);
            return;
        }
        new GetFlickrPicsPageTask(keywords, this, mFlickrApi).execute();
    }

    void requestUpdateFlickrPictures() {
        if (mFlickerPictures != null) {
            updateFlickrPictures(mFlickerPictures);
        }
    }

    void updateFlickrPictures(List<Photo> photos) {
        if (photos == null) {
            mToaster.show(R.string.err_querying_flickr);
        } else {
            mFlickerPictures = photos;
            mView.updateFlickrPictures(mFlickerPictures);
        }
    }
}
