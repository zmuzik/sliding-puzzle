package zmuzik.slidingpuzzle2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.flickr.Photo;
import zmuzik.slidingpuzzle2.ui.activities.GameActivity;

public class FlickrGridAdapter extends PicturesGridAdapter {

    public static final String PHOTO = "PHOTO";

    public List<Photo> mPhotos;

    public FlickrGridAdapter(Context ctx, List<Photo> photos, int columns) {
        super(ctx, null, columns);
        mPhotos = photos;
        mFilePaths = new ArrayList<>();
        for (Photo photo : photos) {
            mFilePaths.add(photo.getThumbUrl());
        }
    }

    @Override public void setOrientationIcon(ImageView orientationIcon, int position) {
        Photo photo = mPhotos.get(position);
        orientationIcon.setVisibility(View.VISIBLE);
        boolean isHorizontal = photo.getWidth_l() > photo.getHeight_l();
        orientationIcon.setRotation(isHorizontal ? 270f : 0f);
    }

    int getPageSize() {
        return Conf.PAGE_SIZE;
    }

    @Override public void runGame(int position) {
        Photo photo = mPhotos.get(position);
        boolean isHorizontal = photo != null && photo.getWidth_l() > photo.getHeight_l();
        Intent intent = new Intent(mContext, GameActivity.class);
        String photoStr = new Gson().toJson(photo);
        intent.putExtra(PHOTO, photoStr);
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
    }
}
