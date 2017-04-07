package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class BasePicturesGridView extends RelativeLayout {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.permissionsCombo)
    LinearLayout mPermissionsCombo;
    @BindView(R.id.permissionMessage)
    TextView mPermissionMessage;
    @BindView(R.id.requestPermissionsButton)
    Button mRequestPermissionsButton;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Inject
    MainScreenPresenter mPresenter;

    PicturesGridAdapter mAdapter;

    public BasePicturesGridView(Context context) {
        super(context);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.pictures_grid, this);
        ButterKnife.bind(this, this);
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).getComponent().inject(this);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumnsNumber()));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        requestUpdate();
    }

    boolean isHorizontal() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    int getColumnsNumber() {
        if (Utils.isTablet(getContext())) {
            return isHorizontal()
                    ? Conf.GRID_COLUMNS_LANDSCAPE_TABLET
                    : Conf.GRID_COLUMNS_PORTRAIT_TABLET;
        } else {
            return isHorizontal()
                    ? Conf.GRID_COLUMNS_LANDSCAPE_HANDHELD
                    : Conf.GRID_COLUMNS_PORTRAIT_HANDHELD;
        }
    }

    public void setFabIcon(Drawable drawable) {
        mFab.setImageDrawable(drawable);
        mFab.setVisibility(VISIBLE);
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View fab) {
    }

    @OnClick(R.id.requestPermissionsButton)
    public void onRequestPermissionsButtonClicked(View fab) {
    }

    public void requestUpdate() {
        // this should call the presenter to request pictures
    }

    // this should be called by the presenter (via proxy/activity) to update the pictures
    public void update(List<String> uris) {
        mProgressBar.setVisibility(GONE);
        mAdapter = new PicturesGridAdapter(getContext(), uris, getColumnsNumber());
        mRecyclerView.setAdapter(mAdapter);
    }
}
