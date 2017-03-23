package zmuzik.slidingpuzzle2.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.Conf;

public class PrefsHelper {

    private static final String GRID_DIM_SHORT = "GRID_DIM_SHORT";
    private static final String GRID_DIM_LONG = "GRID_DIM_LONG";
    private static final String GRID_DIMS_POSITION = "GRID_DIMS_POSITION";
    private static final String DISPLAY_TILE_NUMBERS = "DISPLAY_TILE_NUMBERS";
    private static final String SHOULD_ASK_READ_STORAGE_PERM = "SHOULD_ASK_READ_STORAGE_PERM";

    private static PrefsHelper instance = null;
    private final String PACKAGE_NAME;

    public PrefsHelper() {
        PACKAGE_NAME = App.get().getPackageName();
    }

    public static synchronized PrefsHelper get() {
        if (instance == null) {
            instance = new PrefsHelper();
        }
        return instance;
    }

    private SharedPreferences getPrefs() {
        return App.get().getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public void setGridDimShort(int timestamp) {
        getPrefs().edit().putInt(GRID_DIM_SHORT, timestamp).commit();
    }

    public int getGridDimShort() {
        return getPrefs().getInt(GRID_DIM_SHORT, Conf.DEFAULT_GRID_DIM_SHORT);
    }

    public void setGridDimLong(int timestamp) {
        getPrefs().edit().putInt(GRID_DIM_LONG, timestamp).commit();
    }

    public int getGridDimLong() {
        return getPrefs().getInt(GRID_DIM_LONG, Conf.DEFAULT_GRID_DIM_LONG);
    }

    public void setGridDimsPosition(int timestamp) {
        getPrefs().edit().putInt(GRID_DIMS_POSITION, timestamp).commit();
    }

    public int getGridDimsPosition() {
        return getPrefs().getInt(GRID_DIMS_POSITION, Conf.DEFAULT_GRID_DIMS_POSITION);
    }

    public boolean getDisplayTileNumbers() {
        return getPrefs().getBoolean(DISPLAY_TILE_NUMBERS, Conf.DEFAULT_DISPLAY_TILE_NUMBERS);
    }

    public void setDisplayTileNumbers(boolean yesNo) {
        getPrefs().edit().putBoolean(DISPLAY_TILE_NUMBERS, yesNo).apply();
    }

    public void setShouldAskReadStoragePerm(boolean yesNo) {
        getPrefs().edit().putBoolean(SHOULD_ASK_READ_STORAGE_PERM, yesNo).commit();
    }

    public boolean shouldAskReadStoragePerm() {
        return getPrefs().getBoolean(SHOULD_ASK_READ_STORAGE_PERM, true);
    }
}
