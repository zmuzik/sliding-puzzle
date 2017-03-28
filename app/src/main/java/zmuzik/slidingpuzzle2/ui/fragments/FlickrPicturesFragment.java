package zmuzik.slidingpuzzle2.ui.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.adapters.FlickrGridAdapter;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.flickr.SearchResponse;

public class FlickrPicturesFragment extends SavedPicturesFragment {

    final String TAG = this.getClass().getSimpleName();

    ProgressBar mProgressBar;
    FloatingActionButton mFab;

    private List<Photo> mFlickrPhotos = new ArrayList<>();

    @Inject
    FlickrApi mFlickrApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent(getContext()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFlickrSearchDialog();
            }
        });

        return rootView;
    }

    private void showFlickrSearchDialog() {
        LayoutInflater inflater = FlickrPicturesFragment.this.getLayoutInflater(null);
        View layout = inflater.inflate(R.layout.flickr_search_dialog, null);
        final EditText keywordsEt = (EditText) layout.findViewById(R.id.keywordEt);

        AlertDialog.Builder builder = new AlertDialog.Builder(FlickrPicturesFragment.this.getContext());
        builder.setTitle(R.string.flickr_search);
        builder.setView(layout);
        builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (keywordsEt != null && keywordsEt.getText() != null) {
                    search(keywordsEt.getText().toString());
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        keywordsEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (keywordsEt != null && keywordsEt.getText() != null) {
                        search(keywordsEt.getText().toString());
                    }
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        keywordsEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (keywordsEt != null && keywordsEt.getText() != null) {
                                search(keywordsEt.getText().toString());
                            }
                            dialog.dismiss();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void search(String keywords) {
        if (!Utils.isOnline(getContext())) {
            Toast.makeText(App.get(),
                    App.get().getResources().getString(R.string.internet_unavailable),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (keywords == null || "".equals(keywords.toString())) {
            Toast.makeText(App.get(),
                    App.get().getResources().getString(R.string.keyword_not_supplied),
                    Toast.LENGTH_LONG).show();
            return;
        }

        new GetFlickrPicsPageTask(keywords, mFab).execute();
    }

    public FlickrGridAdapter getAdapter(int columns) {
        return new FlickrGridAdapter(getActivity(), mFlickrPhotos, columns);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public int getLayoutId() {
        return R.layout.fragment_flickr_pictures_grid;
    }

    private class GetFlickrPicsPageTask extends AsyncTask<Void, Void, Void> {

        String query;
        View buttonToDisable;
        SearchResponse resp;

        public GetFlickrPicsPageTask(String query, View v) {
            this.query = query;
            buttonToDisable = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonToDisable.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Call<SearchResponse> call = mFlickrApi.getPhotos(query);
                resp = call.execute().body();
            } catch (Exception e) {
                resp = null;
                Crashlytics.logException(e);
            }
            if (resp != null && resp.getPhotos() != null) {
                mFlickrPhotos = resp.getPhotos().getPhoto();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resp == null) {
                Toast.makeText(App.get(),
                        App.get().getResources().getString(R.string.err_querying_flickr),
                        Toast.LENGTH_LONG).show();
            } else {
                List<Photo> photos = mFlickrPhotos;
                if (photos != null && photos.size() > 0 && isAdded()) {
                    mRecyclerView.setAdapter(new FlickrGridAdapter(getActivity(), photos, getColumnsNumber()));
                }
            }
            buttonToDisable.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
