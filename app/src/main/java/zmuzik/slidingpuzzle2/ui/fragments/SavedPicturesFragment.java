package zmuzik.slidingpuzzle2.ui.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.AppConf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.adapters.PicturesGridAdapter;
import zmuzik.slidingpuzzle2.helpers.BitmapHelper;

public class SavedPicturesFragment extends Fragment {

    final String[] originalPictures = {
            BitmapHelper.ASSET_PREFIX + "game_pic_0.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_1.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_7.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_2.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_3.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_4.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_5.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_6.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_8.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_9.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_10.jpg",
            BitmapHelper.ASSET_PREFIX + "game_pic_11.jpg"};

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    PicturesGridAdapter mAdapter;

    public SavedPicturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        int columns = getColumnsNumber();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = getAdapter(columns);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public PicturesGridAdapter getAdapter(int columns) {
        return new PicturesGridAdapter(getActivity(), getPictures(), columns);
    }

    boolean isHorizontal() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    int getColumnsNumber() {
        if (App.get().isTablet()) {
            return isHorizontal() ? AppConf.GRID_COLUMNS_LANDSCAPE_TABLET : AppConf.GRID_COLUMNS_PORTRAIT_TABLET;
        } else {
            return isHorizontal() ? AppConf.GRID_COLUMNS_LANDSCAPE_HANDHELD : AppConf.GRID_COLUMNS_PORTRAIT_HANDHELD;
        }
    }

    public List<String> getPictures() {
        return Arrays.asList(originalPictures);
    }

    public int getLayoutId() {
        return R.layout.fragment_pictures_grid;
    }
}
