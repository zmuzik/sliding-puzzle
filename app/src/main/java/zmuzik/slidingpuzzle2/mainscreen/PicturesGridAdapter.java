package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.gamescreen.GameActivity;
import zmuzik.slidingpuzzle2.common.view.SquareImageView;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    final String TAG = this.getClass().getSimpleName();

    List<String> mFilePaths;
    Context mContext;

    private int mColumns;
    private int mDim;
    private int mPage = 1;

    @Inject
    MainScreenView mView;
    @Inject
    MainScreenPresenter mPresenter;

    @Inject
    public PicturesGridAdapter(Context ctx, List<String> filePaths, int columns) {
        mContext = ctx;
        mColumns = columns;
        mFilePaths = filePaths;
    }

    @Override
    public PicturesGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pictures_grid, parent, false);
        mDim = parent.getWidth() / mColumns;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (isFooterItem(position)) {
            bindFooterItem(holder, position);
        } else {
            bindNormalItem(holder, position);
        }
    }

    private void bindNormalItem(final ViewHolder holder, final int position) {
        final String uriString = mFilePaths.get(position);
        holder.nextTv.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext).cancelRequest(holder.image);
        Picasso.with(mContext).load(uriString)
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
                runGame(position);
            }
        });
    }

    public void runGame(int position) {
        boolean isHorizontal = Utils.isBitmapHorizontal(mFilePaths.get(position));
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(Keys.PICTURE_URI, mFilePaths.get(position));
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
        mPresenter.runGame(mFilePaths.get(position), isHorizontal);
    }

    private void bindFooterItem(final ViewHolder holder, final int position) {
        holder.image.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
        holder.orientationIcon.setVisibility(View.GONE);
        holder.nextTv.setVisibility(View.VISIBLE);
        holder.nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextPage();
            }
        });
    }

    private void showNextPage() {
        int startPosition = getItemCount() - 1;
        mPage++;
        notifyItemChanged(startPosition++);
        int endPosition = getItemCount();
        for (int i = startPosition; i < endPosition; i++) {
            notifyItemInserted(i);
        }
    }

    public void setOrientationIcon(ImageView orientationIcon, int position) {
        orientationIcon.setVisibility(View.VISIBLE);
        boolean isHorizontal = Utils.isBitmapHorizontal(mFilePaths.get(position));
        orientationIcon.setRotation(isHorizontal ? 270f : 0f);
    }

    int getPageSize() {
        return Conf.PAGE_SIZE;
    }

    private int getDisplayedPicsCount() {
        if (mFilePaths == null) return 0;
        return (mFilePaths.size() < getPageSize() * mPage) ? mFilePaths.size() : getPageSize() * mPage;
    }

    private boolean isMoreToDisplay() {
        if (mFilePaths == null) return false;
        return mFilePaths.size() > getPageSize() * mPage;
    }

    @Override
    public int getItemCount() {
        if (mFilePaths == null) return 0;
        return (isMoreToDisplay()) ? getDisplayedPicsCount() + 1 : getDisplayedPicsCount();
    }

    private boolean isFooterItem(int position) {
        return position == getItemCount() - 1 && isMoreToDisplay();
    }

    public void add(String item, int position) {
        if (mFilePaths == null) mFilePaths = new ArrayList<>();
        if (mFilePaths.contains(item)) return;
        mFilePaths.add(position, item);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        SquareImageView image;
        @BindView(R.id.orientationIcon)
        ImageView orientationIcon;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.nextTv)
        TextView nextTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
