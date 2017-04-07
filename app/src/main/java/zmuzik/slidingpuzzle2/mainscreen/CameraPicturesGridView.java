package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.OnClick;
import zmuzik.slidingpuzzle2.R;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class CameraPicturesGridView extends BasePicturesGridView {

    @BindDrawable(R.drawable.ic_photo_camera_24dp)
    Drawable mFabIcon;

    public CameraPicturesGridView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        mFab.setImageDrawable(mFabIcon);
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
        mProgressBar.setVisibility(VISIBLE);
    }

    @Override
    public void update(List<String> pictures) {
        super.update(pictures);
        mPermissionsCombo.setVisibility(mPresenter.isReadExternalGranted() ? GONE : VISIBLE);
    }
}
