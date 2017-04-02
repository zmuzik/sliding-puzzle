package zmuzik.slidingpuzzle2.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.Utils;
import zmuzik.slidingpuzzle2.common.Keys;
import zmuzik.slidingpuzzle2.common.view.SquareImageView;
import zmuzik.slidingpuzzle2.gamescreen.GameActivity;

public class PicturesGridAdapter extends RecyclerView.Adapter<PicturesGridAdapter.ViewHolder> {

    final String TAG = this.getClass().getSimpleName();

    List<Picture> mPictures;
    Context mContext;

    private int mColumns;
    private int mDim;
    private int mPage = 1;

    @Inject
    MainScreenPresenter mPresenter;

    @Inject
    public PicturesGridAdapter(Context ctx, List<String> uris, int columns) {
        mContext = ctx;
        mColumns = columns;
        mPictures = new ArrayList<>();
        for (String uri : uris) {
            mPictures.add(new Picture(uri));
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
                .transform(new MeasuringSquareTransformation(position))
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
        boolean isHorizontal = Utils.isBitmapHorizontal(mPictures.get(position).uri);
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(Keys.PICTURE_URI, mPictures.get(position).uri);
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal);
        mContext.startActivity(intent);
        mPresenter.runGame(mPictures.get(position).uri, isHorizontal);
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
        Picture picture = mPictures.get(position);
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

    private class MeasuringSquareTransformation implements Transformation {

        int mPos;

        MeasuringSquareTransformation(int position) {
            mPos = position;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Picture picture = mPictures.get(mPos);
            picture.origWidth = source.getWidth();
            picture.origHeight = source.getHeight();

            int squareDim = Math.min(picture.origWidth, picture.origHeight);
            int width = (picture.origWidth - squareDim) / 2;
            int height = (picture.origHeight - squareDim) / 2;
            //crop to square
            Bitmap squareBitmap = Bitmap.createBitmap(source, width, height, squareDim, squareDim);
            if (squareBitmap != source) source.recycle();
            //scale down
            Bitmap result = Bitmap.createScaledBitmap(squareBitmap, mDim, mDim, true);
            if (result != squareBitmap) squareBitmap.recycle();
            return result;
        }

        @Override
        public String key() {
            return "MeasuringSquareTransformation(" + mPos + ")";
        }
    }

    private class Picture {
        String uri;
        int origWidth;
        int origHeight;

        Picture(String uri) {
            this.uri = uri;
        }

        boolean isHorizontal() {
            return origWidth > origHeight;
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
