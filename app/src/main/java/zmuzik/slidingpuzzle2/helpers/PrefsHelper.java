package zmuzik.slidingpuzzle2.helpers;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import zmuzik.slidingpuzzle2.Conf;

@Singleton
public class PrefsHelper {

    private static final String GRID_DIM_SHORT = "GRID_DIM_SHORT";
    private static final String GRID_DIM_LONG = "GRID_DIM_LONG";
    private static final String GRID_DIMS_POSITION = "GRID_DIMS_POSITION";
    private static final String DISPLAY_TILE_NUMBERS = "DISPLAY_TILE_NUMBERS";
    private static final String SHOULD_ASK_READ_STORAGE_PERM = "SHOULD_ASK_READ_STORAGE_PERM";

    private SharedPreferences mSharedPreferences;

    @Inject
    public PrefsHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void setGridDimShort(int timestamp) {
        mSharedPreferences.edit().putInt(GRID_DIM_SHORT, timestamp).commit();
    }

    public int getGridDimShort() {
        return mSharedPreferences.getInt(GRID_DIM_SHORT, Conf.DEFAULT_GRID_DIM_SHORT);
    }

    public void setGridDimLong(int timestamp) {
        mSharedPreferences.edit().putInt(GRID_DIM_LONG, timestamp).commit();
    }

    public int getGridDimLong() {
        return mSharedPreferences.getInt(GRID_DIM_LONG, Conf.DEFAULT_GRID_DIM_LONG);
    }

    public void setGridDimsPosition(int timestamp) {
        mSharedPreferences.edit().putInt(GRID_DIMS_POSITION, timestamp).commit();
    }

    public int getGridDimsPosition() {
        return mSharedPreferences.getInt(GRID_DIMS_POSITION, Conf.DEFAULT_GRID_DIMS_POSITION);
    }

    public boolean getDisplayTileNumbers() {
        return mSharedPreferences.getBoolean(DISPLAY_TILE_NUMBERS, Conf.DEFAULT_DISPLAY_TILE_NUMBERS);
    }

    public void setDisplayTileNumbers(boolean yesNo) {
        mSharedPreferences.edit().putBoolean(DISPLAY_TILE_NUMBERS, yesNo).apply();
    }

    public void setShouldAskReadStoragePerm(boolean yesNo) {
        mSharedPreferences.edit().putBoolean(SHOULD_ASK_READ_STORAGE_PERM, yesNo).commit();
    }

    public boolean shouldAskReadStoragePerm() {
        return mSharedPreferences.getBoolean(SHOULD_ASK_READ_STORAGE_PERM, true);
    }
}
