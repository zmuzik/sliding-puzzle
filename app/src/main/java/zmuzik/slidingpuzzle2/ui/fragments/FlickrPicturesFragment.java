package zmuzik.slidingpuzzle2.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.adapters.FlickrGridAdapter;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.SearchResponse;

public class FlickrPicturesFragment extends SavedPicturesFragment {

    @InjectView(R.id.searchBtn) Button searchBtn;
    @InjectView(R.id.keywordEt) EditText keywordEt;
    @InjectView(R.id.progressBar) ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.inject(this, rootView);
        addKeywordEtListeners();

        return rootView;
    }

    public FlickrGridAdapter getAdapter(int columns) {
        return new FlickrGridAdapter(getActivity(), App.get().getFlickrPhotos(), columns);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public int getLayoutId() {
        return R.layout.fragment_flickr_pictures_grid;
    }

    @OnClick(R.id.searchBtn) void onSearchBtnClick(View v) {
        //hide virtual keyboard
        if (v != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (!App.get().isOnline()) {
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(R.string.internet_unavailable),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (keywordEt == null || keywordEt.getText() == null || "".equals(keywordEt.getText().toString())) {
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(R.string.keyword_not_supplied),
                    Toast.LENGTH_LONG).show();
            return;
        }

        new GetFlickrPicsPageTask(keywordEt.getText().toString(), v).execute();
    }

    void addKeywordEtListeners() {
        keywordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchBtnClick(searchBtn);
                    return true;
                }
                return false;
            }
        });

        keywordEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchBtn.requestFocus();
                            onSearchBtnClick(searchBtn);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private class GetFlickrPicsPageTask extends AsyncTask<Void, Void, Void> {

        String query;
        View buttonToDisable;
        SearchResponse resp;

        public GetFlickrPicsPageTask(String query, View v) {
            this.query = query;
            buttonToDisable = v;
        }

        @Override protected void onPreExecute() {
            super.onPreExecute();
            buttonToDisable.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override protected Void doInBackground(Void... params) {
            String keyword = keywordEt.getText().toString();
            try {
                resp = App.get().getFlickrApi().getPhotos(keyword);
            } catch (Exception e) {
                resp = null;
                Crashlytics.log("keyword = " + (keyword == null ? "" : keyword));
                Crashlytics.logException(e);
            }
            if (resp != null && resp.getPhotos() != null) {
                App.get().setFlickrPhotos(resp.getPhotos().getPhoto());
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resp == null) {
                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.err_querying_flickr),
                        Toast.LENGTH_LONG).show();
            } else {
                List<Photo> photos = App.get().getFlickrPhotos();
                if (photos != null && photos.size() > 0 && isAdded()) {
                    mRecyclerView.setAdapter(new FlickrGridAdapter(getActivity(), photos, getColumnsNumber()));
                }
            }
            buttonToDisable.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}
