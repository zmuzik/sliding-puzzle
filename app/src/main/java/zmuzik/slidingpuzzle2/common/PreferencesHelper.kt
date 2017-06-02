package zmuzik.slidingpuzzle2.common

import android.content.SharedPreferences
import zmuzik.slidingpuzzle2.Conf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject
constructor(private val preferences: SharedPreferences) {

    private val GRID_DIM_SHORT = "GRID_DIM_SHORT"
    private val GRID_DIM_LONG = "GRID_DIM_LONG"
    private val DISPLAY_TILE_NUMBERS = "DISPLAY_TILE_NUMBERS"
    private val SHOULD_ASK_READ_STORAGE_PERM = "SHOULD_ASK_READ_STORAGE_PERM"

    var gridDimShort: Int
        get() = preferences.getInt(GRID_DIM_SHORT, Conf.DEFAULT_GRID_DIM_SHORT)
        set(timestamp) = preferences.edit().putInt(GRID_DIM_SHORT, timestamp).apply()

    var gridDimLong: Int
        get() = preferences.getInt(GRID_DIM_LONG, Conf.DEFAULT_GRID_DIM_LONG)
        set(timestamp) = preferences.edit().putInt(GRID_DIM_LONG, timestamp).apply()

    var displayTileNumbers: Boolean
        get() = preferences.getBoolean(DISPLAY_TILE_NUMBERS, Conf.DEFAULT_DISPLAY_TILE_NUMBERS)
        set(yesNo) = preferences.edit().putBoolean(DISPLAY_TILE_NUMBERS, yesNo).apply()

    var shouldAskReadStoragePerm: Boolean
        get() = preferences.getBoolean(SHOULD_ASK_READ_STORAGE_PERM, true)
        set(yesNo) = preferences.edit().putBoolean(SHOULD_ASK_READ_STORAGE_PERM, yesNo).apply()
}
