package zmuzik.slidingpuzzle2.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.view.BasePicturesGridView;
import zmuzik.slidingpuzzle2.view.SavedPicturesGridView;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

public class MainScreenPagerAdapter extends PagerAdapter {

    private Context mContext;

    public MainScreenPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        BasePicturesGridView gridView = new SavedPicturesGridView(mContext);
        gridView.requestUpdate();
        collection.addView(gridView );
        return gridView ;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.title_section1);
            case 1:
                return mContext.getResources().getString(R.string.title_section2);
            case 2:
                return mContext.getResources().getString(R.string.title_section3);
            default:
                return "";
        }
    }
}
