package zmuzik.slidingpuzzle2

object Conf {
    @JvmField val GRID_SIZES = arrayOf("3x3", "3x4", "3x5", "3x6", "4x4", "4x5", "4x6", "5x5", "5x6", "6x6")

    const val DEFAULT_GRID_DIM_SHORT = 4
    const val DEFAULT_GRID_DIM_LONG = 4
    const val DEFAULT_GRID_DIMS_POSITION = 4

    const val DEFAULT_DISPLAY_TILE_NUMBERS = true

    const val PAGE_SIZE = 12

    const val FLICKR_REQUEST_IMAGES = 48

    const val GRID_COLS_PORTRAIT_PHONE = 2
    const val GRID_COLS_LANDSCAPE_PHONE = 3
    const val GRID_COLS_PORTRAIT_TABLET = 4
    const val GRID_COLS_LANDSCAPE_TABLET = 6
}
