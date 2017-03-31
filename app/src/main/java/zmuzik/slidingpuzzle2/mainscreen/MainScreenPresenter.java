package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;
import zmuzik.slidingpuzzle2.gamescreen.GameActivity;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
public class MainScreenPresenter {

    private final String TAG = this.getClass().getSimpleName();

    @Inject
    PrefsHelper mPrefsHelper;

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
}
