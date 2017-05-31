package zmuzik.slidingpuzzle2.common

import android.content.SharedPreferences
import zmuzik.slidingpuzzle2.Conf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject
constructor(private val mSharedPreferences: SharedPreferences) {

    companion object {
        private val GRID_DIM_SHORT = "GRID_DIM_SHORT"
        private val GRID_DIM_LONG = "GRID_DIM_LONG"
        private val DISPLAY_TILE_NUMBERS = "DISPLAY_TILE_NUMBERS"
        private val SHOULD_ASK_READ_STORAGE_PERM = "SHOULD_ASK_READ_STORAGE_PERM"
    }

    var gridDimShort: Int
        get() = mSharedPreferences.getInt(GRID_DIM_SHORT, Conf.DEFAULT_GRID_DIM_SHORT)
        set(timestamp) = mSharedPreferences.edit().putInt(GRID_DIM_SHORT, timestamp).apply()

    var gridDimLong: Int
        get() = mSharedPreferences.getInt(GRID_DIM_LONG, Conf.DEFAULT_GRID_DIM_LONG)
        set(timestamp) = mSharedPreferences.edit().putInt(GRID_DIM_LONG, timestamp).apply()

    var displayTileNumbers: Boolean
        get() = mSharedPreferences.getBoolean(DISPLAY_TILE_NUMBERS, Conf.DEFAULT_DISPLAY_TILE_NUMBERS)
        set(yesNo) = mSharedPreferences.edit().putBoolean(DISPLAY_TILE_NUMBERS, yesNo).apply()

    fun setShouldAskReadStoragePerm(yesNo: Boolean) {
        mSharedPreferences.edit().putBoolean(SHOULD_ASK_READ_STORAGE_PERM, yesNo).apply()
    }

    fun shouldAskReadStoragePerm(): Boolean {
        return mSharedPreferences.getBoolean(SHOULD_ASK_READ_STORAGE_PERM, true)
    }
}
