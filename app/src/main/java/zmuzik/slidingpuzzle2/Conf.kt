package zmuzik.slidingpuzzle2

import zmuzik.slidingpuzzle2.common.ASSET_PREFIX

object Conf {
    val GRID_SIZES = arrayOf("3x3", "3x4", "3x5", "3x6", "4x4", "4x5", "4x6", "5x5", "5x6", "6x6")

    val APP_PHOTOS = arrayOf(
            ASSET_PREFIX + "game_pic_00.jpg",
            ASSET_PREFIX + "game_pic_01.jpg",
            ASSET_PREFIX + "game_pic_07.jpg",
            ASSET_PREFIX + "game_pic_02.jpg",
            ASSET_PREFIX + "game_pic_03.jpg",
            ASSET_PREFIX + "game_pic_04.jpg",
            ASSET_PREFIX + "game_pic_05.jpg",
            ASSET_PREFIX + "game_pic_06.jpg",
            ASSET_PREFIX + "game_pic_08.jpg",
            ASSET_PREFIX + "game_pic_09.jpg",
            ASSET_PREFIX + "game_pic_10.jpg",
            ASSET_PREFIX + "game_pic_11.jpg")

    const val DEFAULT_GRID_DIM_SHORT = 4
    const val DEFAULT_GRID_DIM_LONG = 4

    const val DEFAULT_DISPLAY_TILE_NUMBERS = true

    const val FLICKR_REQUEST_IMAGES = 96

    const val GRID_COLS_PHONE_PORTRAIT = 2
    const val GRID_COLS_PHONE_LANDSCAPE = 3
    const val GRID_COLS_TABLET_PORTRAIT = 4
    const val GRID_COLS_TABLET_LANDSCAPE = 6
}
