package zmuzik.slidingpuzzle.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.adapters.PicturesGridAdapter;
import zmuzik.slidingpuzzle.flickr.Photo;
import zmuzik.slidingpuzzle.flickr.SearchResponse;

public class FlickrPicturesFragment extends SavedPicturesFragment {

    @InjectView(R.id.searchBtn) Button searchBtn;
    @InjectView(R.id.keywordEt) EditText keywordEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public List<String> getPictures() {
        return new ArrayList<>();
    }

    public int getLayoutId() {
        return R.layout.fragment_flickr_pictures_grid;
    }

    @OnClick(R.id.searchBtn) void onClick(View v) {
        //hide virtual keyboard
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        new FlickrCaller(keywordEt.getText().toString(), v).execute();
    }

    private class FlickrCaller extends AsyncTask<Void, Void, Void> {

        String query;
        List<Photo> photos;
        View buttonToDisable;

        public FlickrCaller(String query, View v) {
            this.query = query;
            buttonToDisable = v;
        }

        @Override protected void onPreExecute() {
            super.onPreExecute();
            buttonToDisable.setEnabled(false);
        }

        @Override protected Void doInBackground(Void... params) {
            SearchResponse resp = App.get().getFlickrApi().getPhotos(keywordEt.getText().toString());
            photos = resp.getPhotos().getPhoto();
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            PicturesGridAdapter adapter = (PicturesGridAdapter) mRecyclerView.getAdapter();
            //remove all items in the list
            while (adapter.getItemCount() > 0) {
                adapter.remove(0);
            }
            //add new photos
            for (Photo photo : photos) {
                adapter.add(photo.getThumbUrl());
            }
            buttonToDisable.setEnabled(true);
        }
    }
}
