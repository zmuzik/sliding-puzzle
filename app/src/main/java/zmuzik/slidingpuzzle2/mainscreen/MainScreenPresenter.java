package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.gamescreen.GameActivity;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
public class MainScreenPresenter {

    private final String TAG = this.getClass().getSimpleName();

    final String[] originalPictures = {
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
    MainScreenView mView;

    @Inject
    Context mContext;

    @Inject
    public MainScreenPresenter() {
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

    public void runGame(String pictureUri, boolean isHorizontal) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(Keys.PICTURE_URI, pictureUri);
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
    }

    public List<String> getSavedPicturesList() {
        return Arrays.asList(originalPictures);
    }
}
