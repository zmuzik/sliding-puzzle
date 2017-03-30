package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.di.ActivityScope;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
public class MainScreenPresenter {

    final String TAG = this.getClass().getSimpleName();

    @Inject
    PrefsHelper mPrefsHelper;

    @Inject
    MainScreenView mView;

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
}
