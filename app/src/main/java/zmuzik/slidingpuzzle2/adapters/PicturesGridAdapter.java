package zmuzik.slidingpuzzle2.adapters;

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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.gfx.SquareImageView;
import zmuzik.slidingpuzzle2.helpers.BitmapHelper;
import zmuzik.slidingpuzzle2.ui.activities.GameActivity;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    final String TAG = this.getClass().getSimpleName();
    public static final String FILE_URI = "FILE_URI";

    protected List<String> mFilePaths;
    protected Context mContext;
    protected int mColumns;
    protected int mDim;
    protected int mPage = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView image;
        public ImageView orientationIcon;
        public ProgressBar progressBar;
        public TextView nextTv;

        public ViewHolder(View v) {
            super(v);
            image = (SquareImageView) v.findViewById(R.id.image);
            orientationIcon = (ImageView) v.findViewById(R.id.orientationIcon);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            nextTv = (TextView) v.findViewById(R.id.nextTv);
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
        if (isFooterItem(position)) {
            bindFooterItem(holder, position);
        } else {
            bindNormalItem(holder, position);
        }
    }

    void bindNormalItem(final ViewHolder holder, final int position) {
        final String uriString = mFilePaths.get(position);
        holder.nextTv.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);
        Crashlytics.log(Log.DEBUG, TAG, "canceling Picasso request");
        Picasso.with(App.get()).cancelRequest(holder.image);
        Crashlytics.log(Log.DEBUG, TAG, "requesting " + uriString);
        Picasso.with(App.get()).load(uriString)
                .resize(mDim, mDim)
                .centerCrop()
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.orientationIcon.setVisibility(View.VISIBLE);
                        setOrientationIcon(holder.orientationIcon, position);
                    }

                    @Override
                    public void onError() {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.orientationIcon.setVisibility(View.GONE);
                        holder.image.setImageResource(R.drawable.ic_panorama_32dp);
                        Log.e("PicturesGridAdapter", uriString + " failed");
                    }
                });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.log(Log.DEBUG, TAG, "run game");
                runGame(position);
            }
        });
    }

    public void runGame(int position) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(FILE_URI, mFilePaths.get(position));
        mContext.startActivity(intent);
    }

    void bindFooterItem(final ViewHolder holder, final int position) {
        //holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.nothing));
        holder.image.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
        holder.orientationIcon.setVisibility(View.GONE);
        holder.nextTv.setVisibility(View.VISIBLE);
        holder.nextTv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Crashlytics.log(Log.DEBUG, TAG, "more clicked");
                showNextPage();
            }
        });
    }

    void showNextPage() {
        int startPosition = getItemCount() - 1;
        mPage++;
        notifyItemChanged(startPosition++);
        int endPosition = getItemCount();
        for (int i = startPosition; i < endPosition; i++) {
            notifyItemInserted(i);
        }
    }

    public void setOrientationIcon(ImageView orientationIcon, int position) {
        Resources res = App.get().getResources();
        orientationIcon.setVisibility(View.VISIBLE);
        boolean isHorizontal = BitmapHelper.isBitmapHorizontal(mFilePaths.get(position));
        orientationIcon.setRotation(isHorizontal ? 270f : 0f);
    }

    int getPageSize() {
        return Conf.PAGE_SIZE_LOCAL;
    }

    int getDisplayedPicsCount() {
        if (mFilePaths == null) return 0;
        return (mFilePaths.size() < getPageSize() * mPage) ? mFilePaths.size() : getPageSize() * mPage;
    }

    boolean isMoreToDisplay() {
        if (mFilePaths == null) return false;
        return mFilePaths.size() > getPageSize() * mPage;
    }

    @Override
    public int getItemCount() {
        if (mFilePaths == null) return 0;
        return (isMoreToDisplay()) ? getDisplayedPicsCount() + 1 : getDisplayedPicsCount();
    }

    boolean isFooterItem(int position) {
        return position == getItemCount() - 1 && isMoreToDisplay();
    }

    public void add(String item, int position) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mFilePaths.contains(item)) return;
        mFilePaths.add(position, item);
        notifyItemInserted(position);
    }
}
