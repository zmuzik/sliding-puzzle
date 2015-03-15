package zmuzik.slidingpuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {

    private static final String GRID_DIM_SHORT = "GRID_DIM_SHORT";
    private static final String GRID_DIM_LONG = "GRID_DIM_LONG";

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
}
