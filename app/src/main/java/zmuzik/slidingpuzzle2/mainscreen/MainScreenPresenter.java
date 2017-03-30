package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

public class MainScreenPresenter {

    final String TAG = this.getClass().getSimpleName();

    final String[] GRID_SIZES = {
            "3x3", "3x4", "3x5", "3x6",
            "4x4", "4x5", "4x6",
            "5x5", "5x6", "6x6",};

    @Inject
    PrefsHelper mPrefsHelper;

    @Inject
    Context mContext;

    public void changeGridSize() {

    }

    public void toggleShowNumbers() {
        boolean value = mPrefsHelper.getDisplayTileNumbers();
        value = !value;
        mPrefsHelper.setDisplayTileNumbers(value);
        String msg = mContext.getString(value ? R.string.display_tile_numbers_on : R.string.display_tile_numbers_off);
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getGridDimensions() {
        return mPrefsHelper.getGridDimShort() + "x" + mPrefsHelper.getGridDimLong();
    }

    public void setGridDimensions(int gridDimShort, int gridDimLong) {
        mPrefsHelper.setGridDimShort(gridDimShort);
        mPrefsHelper.setGridDimLong(gridDimLong);
    }
}
