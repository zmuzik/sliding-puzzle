package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
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
import zmuzik.slidingpuzzle2.common.view.SquareImageView;
import zmuzik.slidingpuzzle2.flickr.Photo;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    final String TAG = this.getClass().getSimpleName();

    List<OrientedPicture> mPictures;
    Context mContext;

    private int mColumns;
    private int mDim;
    private int mPage = 1;

    @Inject
    MainScreenPresenter mPresenter;

    public PicturesGridAdapter(Context ctx, List<String> uris, int columns) {
        mContext = ctx;
        mColumns = columns;
        mPictures = new ArrayList<>();
        for (String uri : uris) {
            mPictures.add(new OrientedPicture(uri));
        }
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).getComponent().inject(this);
        }
    }

    public PicturesGridAdapter(Context ctx, int columns, List<Photo> flickrPhotos) {
        mContext = ctx;
        mColumns = columns;
        mPictures = new ArrayList<>();
        for (Photo flickrPhoto : flickrPhotos) {
            OrientedPicture picture = new OrientedPicture(flickrPhoto.getThumbUrl());
            picture.setIsHorizontal(flickrPhoto.getWidth_l() > flickrPhoto.getHeight_l());
            mPictures.add(picture);
        }
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).getComponent().inject(this);
        }
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
        final String uriString = mPictures.get(position).uri;
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
        OrientedPicture picture = mPictures.get(position);
        mPresenter.runGame(picture.uri, picture.isHorizontal());
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
        OrientedPicture picture = mPictures.get(position);

        orientationIcon.setRotation(picture.isHorizontal() ? 270f : 0f);
    }

    int getPageSize() {
        return Conf.PAGE_SIZE;
    }

    private int getDisplayedPicsCount() {
        if (mPictures == null) return 0;
        return (mPictures.size() < getPageSize() * mPage) ? mPictures.size() : getPageSize() * mPage;
    }

    private boolean isMoreToDisplay() {
        if (mPictures == null) return false;
        return mPictures.size() > getPageSize() * mPage;
    }

    @Override
    public int getItemCount() {
        if (mPictures == null) return 0;
        return (isMoreToDisplay()) ? getDisplayedPicsCount() + 1 : getDisplayedPicsCount();
    }

    private boolean isFooterItem(int position) {
        return position == getItemCount() - 1 && isMoreToDisplay();
    }

    private class OrientedPicture {
        String uri;
        Boolean isHorizontal;

        OrientedPicture(String uri) {
            this.uri = uri;
        }

        void setIsHorizontal(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }

        boolean isHorizontal() {
            if (isHorizontal == null) {
                isHorizontal = Utils.isBitmapHorizontal(uri);
            }
            return isHorizontal;
        }
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
