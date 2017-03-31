package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class SavedPicturesGridView extends BasePicturesGridView {

    public SavedPicturesGridView(Context context) {
        super(context);
    }

    @Override
    public void requestUpdate() {
        mPresenter.requestUpdateSavedPictures();
    }
}
