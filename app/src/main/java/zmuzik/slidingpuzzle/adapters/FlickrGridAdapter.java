package zmuzik.slidingpuzzle.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.flickr.Photo;
import zmuzik.slidingpuzzle.ui.activities.GameActivity;

public class FlickrGridAdapter extends PicturesGridAdapter {

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
        Resources res = App.get().getResources();
        Photo photo = mPhotos.get(position);
        if (photo.getWidth_l() > photo.getHeight_l()) {
            orientationIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_action_hardware_phone_android_horiz));
        } else {
            orientationIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_action_hardware_phone_android));
        }
    }

    public void add(Photo photo) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mPhotos == null) mPhotos = new ArrayList<>();
        if (mFilePaths.contains(photo.getThumbUrl())) return;
        mFilePaths.add(photo.getThumbUrl());
        mPhotos.add(photo);
        notifyItemInserted(getItemCount() - 1);
    }

    public void add(Photo photo, int position) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mPhotos == null) mPhotos = new ArrayList<>();
        if (mFilePaths.contains(photo.getThumbUrl())) return;
        mFilePaths.add(position, photo.getThumbUrl());
        mPhotos.add(position, photo);
        notifyItemInserted(position);
    }

    public void remove(Photo photo) {
        if (mFilePaths == null) return;
        int position = mFilePaths.indexOf(photo.getThumbUrl());
        mFilePaths.remove(position);
        mPhotos.remove(position);
        notifyItemRemoved(position);
    }

    @Override public void remove(int position) {
        if (mFilePaths == null) return;
        mFilePaths.remove(position);
        mPhotos.remove(position);
        notifyItemRemoved(position);
    }

    @Override public void runGame(int position) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra("FILE_URI", mPhotos.get(position).getThumbUrl());
        mContext.startActivity(intent);
    }
}
