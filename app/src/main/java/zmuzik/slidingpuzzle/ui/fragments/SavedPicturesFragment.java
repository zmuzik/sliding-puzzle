package zmuzik.slidingpuzzle.ui.fragments;

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

import zmuzik.slidingpuzzle.Conf;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.adapters.PicturesGridAdapter;
import zmuzik.slidingpuzzle.helpers.BitmapHelper;

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
            BitmapHelper.ASSET_PREFIX + "game_pic_9.jpg"};

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    PicturesGridAdapter mAdapter;

    public SavedPicturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pictures_grid, container, false);

        int columns = (isHorizontal()) ? Conf.GRID_COLUMNS_LANDSCAPE : Conf.GRID_COLUMNS_PORTRAIT;

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PicturesGridAdapter(getActivity(), getPictures(), columns);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    boolean isHorizontal() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public List<String> getPictures() {
        return Arrays.asList(originalPictures);
    }
}
