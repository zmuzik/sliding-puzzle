package zmuzik.slidingpuzzle.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import zmuzik.slidingpuzzle.App;
import zmuzik.slidingpuzzle.R;
import zmuzik.slidingpuzzle.gfx.SquareImageView;
import zmuzik.slidingpuzzle.helpers.BitmapHelper;
import zmuzik.slidingpuzzle.ui.activities.GameActivity;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    protected List<String> mFilePaths;
    protected Context mContext;
    protected int mColumns;
    protected int mDim;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView image;
        public ImageView orientationIcon;
        public ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            image = (SquareImageView) v.findViewById(R.id.image);
            orientationIcon = (ImageView) v.findViewById(R.id.orientationIcon);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
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
                        holder.progressBar.setVisibility(View.GONE);
                        setOrientationIcon(holder.orientationIcon, position);
                    }

                    @Override public void onError() {
                        Toast.makeText(mContext, uriString + " failed", Toast.LENGTH_SHORT).show();
                        Log.e("PicturesGridAdapter", uriString + " failed");
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

    public void setOrientationIcon(ImageView orientationIcon, int position) {
        Resources res = App.get().getResources();
        if (BitmapHelper.isBitmapHorizontal(mFilePaths.get(position))) {
            orientationIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_action_hardware_phone_android_horiz));
        } else {
            orientationIcon.setImageDrawable(
                    res.getDrawable(R.drawable.ic_action_hardware_phone_android));
        }
    }

    @Override
    public int getItemCount() {
        return (mFilePaths == null) ? 0 : mFilePaths.size();
    }

    public void add(String item) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mFilePaths.contains(item)) return;
        mFilePaths.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public void add(String item, int position) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mFilePaths.contains(item)) return;
        mFilePaths.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        if (mFilePaths == null) return;
        int position = mFilePaths.indexOf(item);
        mFilePaths.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        if (mFilePaths == null) return;
        mFilePaths.remove(position);
        notifyItemRemoved(position);
    }
}
