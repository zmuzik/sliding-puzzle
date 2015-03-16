package zmuzik.slidingpuzzle.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.SquareImageView;
import zmuzik.slidingpuzzle.helpers.BitmapHelper;

public class GridViewAdapter extends BaseAdapter {

    String[] originalPictures = {
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

    @Override public int getCount() {
        return originalPictures.length;
    }

    @Override public Object getItem(int i) {
        return originalPictures[i];
    }

    @Override public long getItemId(int i) {
        return i;
    }

    @Override public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.item_picture_grid, viewGroup, false);
        }

        SquareImageView image = (SquareImageView) view.findViewById(R.id.image);
        final ImageView orientationIcon = (ImageView) view.findViewById(R.id.orientationIcon);

        int dim = 240;
        Picasso.with(App.get()).load(originalPictures[i])
                .resize(dim, dim)
                .centerCrop()
                .into(image, new Callback() {
                    @Override public void onSuccess() {
                        Resources res = App.get().getResources();
                        if (BitmapHelper.isBitmapHorizontal(originalPictures[i])) {
                            orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android_horiz));
                        } else {
                            orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android));
                        }
                    }

                    @Override public void onError() {

                    }
                });
        return view;
    }
}
