package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class SavedPicturesGridView extends BasePicturesGridView {

    public SavedPicturesGridView(Context context) {
        super(context);
    }

    public SavedPicturesGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void requestUpdate() {
        // synchronous update, no need to load pictures list in the background
        update();
    }

    @Override
    public List<String> getPictures() {
        return mPresenter.getSavedPicturesList();
    }
}
