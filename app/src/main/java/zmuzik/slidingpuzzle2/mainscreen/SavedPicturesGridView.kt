package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

class SavedPicturesGridView(context: Context) : BasePicturesGridView(context) {

    override fun requestUpdate() {
        presenter.requestUpdateSavedPictures()
    }
}
