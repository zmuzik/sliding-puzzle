package zmuzik.slidingpuzzle2.screens.game

import android.os.Bundle
import androidx.lifecycle.ViewModel
import zmuzik.slidingpuzzle2.common.Keys


class GameScreenViewModel : ViewModel() {

    var pictureUri: String? = null
    var thumbnailDim: Int = 0
    var thumbnailLeft: Int = 0
    var thumbnailTop: Int = 0

    fun initFromIntent(bundle: Bundle) {
        pictureUri = bundle.getString(Keys.PICTURE_URI)
        thumbnailDim = bundle.getInt(Keys.THUMBNAIL_DIM)
        thumbnailLeft = bundle.getInt(Keys.THUMBNAIL_LEFT)
        thumbnailTop = bundle.getInt(Keys.THUMBNAIL_TOP)
    }

    var storedPositions: ArrayList<Int>? = null
    var storedBlackX: Int? = null
    var storedBlackY: Int? = null
    var storedBoardState: GameState? = null
}
