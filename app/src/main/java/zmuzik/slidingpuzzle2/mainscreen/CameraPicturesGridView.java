package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.view.View;

import java.util.List;

import butterknife.OnClick;
import zmuzik.slidingpuzzle2.R;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class CameraPicturesGridView extends BasePicturesGridView {

    public CameraPicturesGridView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        mFab.setVisibility(VISIBLE);
        mPermissionsCombo.setVisibility(mPresenter.isReadExternalGranted() ? GONE : VISIBLE);
    }

    @OnClick(R.id.requestPermissionsButton)
    public void onRequestPermissionsButtonClicked(View fab) {
        mPresenter.requestReadExternalPermission();
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View fab) {
        mPresenter.launchCameraApp();
    }

    @Override
    public void requestUpdate() {
        mPresenter.requestUpdateCameraPictures();
    }

    @Override
    public void update(List<String> pictures) {
        super.update(pictures);
        mPermissionsCombo.setVisibility(mPresenter.isReadExternalGranted() ? GONE : VISIBLE);
    }
}
