package zmuzik.slidingpuzzle.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.SquareImageView;
import zmuzik.slidingpuzzle.helpers.BitmapHelper;
import zmuzik.slidingpuzzle.ui.activities.GameActivity;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    private List<String> mFilePaths;
    private Context mContext;
    private int mColumns;
    int mDim;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView image;
        public ImageView orientationIcon;

        public ViewHolder(View v) {
            super(v);
            image = (SquareImageView) v.findViewById(R.id.image);
            orientationIcon = (ImageView) v.findViewById(R.id.orientationIcon);
        }
    }

    public PicturesGridAdapter(Context ctx, List<String> filePaths, int columns) {
        mContext = ctx;
        mColumns = columns;
        mFilePaths = filePaths;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PicturesGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictures_grid, parent, false);
        // set the view's size, margins, paddings and layout parameters
        mDim = parent.getWidth() / mColumns;
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String uriString = mFilePaths.get(position);
        Picasso.with(App.get()).load(uriString)
                .resize(mDim, mDim)
                .centerCrop()
                .into(holder.image, new Callback() {
                    @Override public void onSuccess() {
                        Resources res = App.get().getResources();
                        if (BitmapHelper.isBitmapHorizontal(uriString)) {
                            holder.orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android_horiz));
                        } else {
                            holder.orientationIcon.setImageDrawable(res.getDrawable(R.drawable.ic_action_hardware_phone_android));
                        }
                    }

                    @Override public void onError() {
                    }
                });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(mContext, GameActivity.class);
                intent.putExtra("FILE_URI", mFilePaths.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mFilePaths == null) ? 0 : mFilePaths.size();
    }
}
