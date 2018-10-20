package zmuzik.slidingpuzzle2.screens.game

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.ViewModel
import zmuzik.slidingpuzzle2.common.Keys
import zmuzik.slidingpuzzle2.common.Prefs
import zmuzik.slidingpuzzle2.repo.Repo

class GameScreenViewModel(val repo: Repo, val prefs: Prefs) : ViewModel() {

    var pictureUri: String? = null
    var thumbnailDim: Int? = null
    var thumbnailBitmap: Bitmap? = null

    fun initFromIntent(bundle: Bundle) {
        pictureUri = bundle.getString(Keys.PICTURE_URI)
        thumbnailDim = bundle.getInt(Keys.THUMBNAIL_DIM)
        thumbnailBitmap = bundle.getParcelable(Keys.THUMBNAIL_BITMAP)
    }

    var storedPositions: ArrayList<Int>? = null
    var storedBlackX: Int? = null
    var storedBlackY: Int? = null
    var storedBoardState: PuzzleBoardView.State? = null
}
