package zmuzik.slidingpuzzle2

object Conf {
    @JvmField val GRID_SIZES = arrayOf("3x3", "3x4", "3x5", "3x6", "4x4", "4x5", "4x6", "5x5", "5x6", "6x6")

    @JvmField val DEFAULT_GRID_DIM_SHORT = 4
    @JvmField val DEFAULT_GRID_DIM_LONG = 4
    @JvmField val DEFAULT_GRID_DIMS_POSITION = 4

    @JvmField val DEFAULT_DISPLAY_TILE_NUMBERS = true

    @JvmField val PAGE_SIZE = 12

    const val FLICKR_REQUEST_IMAGES = 48

    @JvmField val GRID_COLUMNS_PORTRAIT_HANDHELD = 2
    @JvmField val GRID_COLUMNS_LANDSCAPE_HANDHELD = 3
    @JvmField val GRID_COLUMNS_PORTRAIT_TABLET = 4
    @JvmField val GRID_COLUMNS_LANDSCAPE_TABLET = 6
}
