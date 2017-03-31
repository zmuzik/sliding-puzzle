package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;

import java.util.List;

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

    @Override
    public void update(List<String> pictures) {
        mAdapter = new PicturesGridAdapter(getContext(), pictures, getColumnsNumber());
        mRecyclerView.setAdapter(mAdapter);
    }
}
