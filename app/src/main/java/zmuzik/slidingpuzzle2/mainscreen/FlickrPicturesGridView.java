package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.OnClick;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.flickr.Photo;

/**
 * Created by Zbynek Muzik on 2017-03-31.
 */

class FlickrPicturesGridView extends BasePicturesGridView {

    @BindDrawable(R.drawable.ic_search_24dp)
    Drawable mFabIcon;

    public FlickrPicturesGridView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        mFab.setImageDrawable(mFabIcon);
        mFab.setVisibility(VISIBLE);
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View fab) {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.flickr_search_dialog, null);
        final EditText keywordsEt = (EditText) layout.findViewById(R.id.keywordEt);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        mProgressBar.setVisibility(VISIBLE);
        mPresenter.requestFlickrSearch(keywords);
    }

    @Override
    public void requestUpdate() {
        mPresenter.requestUpdateFlickrPictures();
    }

    public void updatePhotos(List<Photo> photos) {
        mProgressBar.setVisibility(GONE);
        mAdapter = new FlickrGridAdapter(getContext(), photos, getColumnsNumber());
        mRecyclerView.setAdapter(mAdapter);
    }
}
