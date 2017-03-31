package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class CameraPicturesGridView extends BasePicturesGridView {

    public CameraPicturesGridView(Context context) {
        super(context);
    }

    @Override
    public void requestUpdate() {
        mPresenter.requestUpdateCameraPictures();
    }

}
