package zmuzik.slidingpuzzle.adapters;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.SquareImageView;

public class GridViewAdapter extends BaseAdapter {

    int[] thumbs = {
            R.drawable.game_pic_0,
            R.drawable.game_pic_1,
            R.drawable.game_pic_2,
            R.drawable.game_pic_3,
            R.drawable.game_pic_4,
            R.drawable.game_pic_5,
            R.drawable.game_pic_6,
            R.drawable.game_pic_7,
            R.drawable.game_pic_8,
            R.drawable.game_pic_9,
    };

    @Override public int getCount() {
        return thumbs.length;
    }

    @Override public Object getItem(int i) {
        return "Item " + String.valueOf(i + 1);
    }

    @Override public long getItemId(int i) {
        return i;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.item_picture_grid, viewGroup, false);
        }

//            String imageUrl = "http://lorempixel.com/800/600/sports/" + String.valueOf(i + 1);
//            view.setTag(imageUrl);
//
//            ImageView image = (ImageView) view.findViewById(R.id.image);
//            Picasso.with(view.getContext())
//                    .load(imageUrl)
//                    .into(image);
        Resources res = App.get().getResources();

        SquareImageView image = (SquareImageView) view.findViewById(R.id.image);
        ImageView orientationIcon = (ImageView) view.findViewById(R.id.orientationIcon);

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Drawable pictureDrawable = res.getDrawable(thumbs[i]);
        image.setImageDrawable(pictureDrawable);
        int width = pictureDrawable.getIntrinsicWidth();
        int height = pictureDrawable.getIntrinsicHeight();

        if (width > height) {
            orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android_horiz));
        } else {
            orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android));
        }
        return view;
    }
}
