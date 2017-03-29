package zmuzik.slidingpuzzle2.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.adapters.PicturesGridAdapter;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class BasePicturesGridView extends RelativeLayout {

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    LinearLayout mPermissionsCombo;
    TextView mPermissionMessage;
    Button mRequestPermissionsButton;
    FloatingActionButton mFab;

    PicturesGridAdapter mAdapter;

    public BasePicturesGridView(Context context) {
        super(context);
        init();
    }

    public BasePicturesGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.pictures_grid, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPermissionsCombo = (LinearLayout) findViewById(R.id.permissionsCombo);
        mPermissionMessage = (TextView) findViewById(R.id.permissionMessage);
        mRequestPermissionsButton = (Button) findViewById(R.id.requestPermissionsButton);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getColumnsNumber()));
        mRequestPermissionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestPermissionsButtonClicked(v);
            }
        });
        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClicked(v);
            }
        });
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

    public void onFabClicked(View fab) {
    }

    public void onRequestPermissionsButtonClicked(View fab) {
    }

    public void requestUpdate() {
    }

    public void update() {
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    public PicturesGridAdapter getAdapter() {
        return new PicturesGridAdapter(getContext(), getPictures(), getColumnsNumber());
    }

    public List<String> getPictures() {
        return null;
    }
}
