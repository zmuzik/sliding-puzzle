package zmuzik.slidingpuzzle2.mainscreen;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.common.Toaster;
import zmuzik.slidingpuzzle2.flickr.Photo;


public class MainActivity extends AppCompatActivity implements MainScreenView {

    final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindString(R.string.title_section1)
    String mTitleSection1;
    @BindString(R.string.title_section2)
    String mTitleSection2;
    @BindString(R.string.title_section3)
    String mTitleSection3;
    @BindString(R.string.select_grid_size)
    String mSelectGridSize;
    @BindString(R.string.grid_size_selected_to)
    String mGridSizeSelectedTo;

    MainActivityComponent mComponent;
    WeakReference<SavedPicturesGridView> mSavedPicturesView;
    WeakReference<CameraPicturesGridView> mCameraPicturesView;
    WeakReference<FlickrPicturesGridView> mFlickrPicturesView;

    @Inject
    MainScreenPresenter mPresenter;
    @Inject
    Toaster mToaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    private void inject() {
        mComponent = DaggerMainActivityComponent.builder()
                .appComponent(((App) getApplication()).getComponent(this))
                .mainActivityModule(new MainActivityModule(this))
                .build();
        mComponent.inject(this);
        mComponent.inject(mPresenter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_grid_size:
                openChangeGridSizeDialog();
                return true;
            case R.id.action_toggle_display_numbers:
                toggleShowNumbers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleShowNumbers() {
        boolean onOff = mPresenter.toggleShowNumbers();
        mToaster.show(onOff ? R.string.display_tile_numbers_on : R.string.display_tile_numbers_off);
    }

    private void openChangeGridSizeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mSelectGridSize);
        builder.setSingleChoiceItems(Conf.GRID_SIZES, getGridDimsPosition(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String positionsStr = Conf.GRID_SIZES[item];
                StringTokenizer tokenizer = new StringTokenizer(positionsStr, "x");
                String shorterStr = tokenizer.nextToken();
                String longerStr = tokenizer.nextToken();
                mPresenter.setGridDimensions(Integer.parseInt(shorterStr), Integer.parseInt(longerStr));
                dialog.dismiss();
                mToaster.show(String.format(mGridSizeSelectedTo, positionsStr));
            }
        });
        builder.show();
    }

    private int getGridDimsPosition() {
        String currentDims = mPresenter.getGridDimensions();
        for (int i = 0; i < Conf.GRID_SIZES.length; i++) {
            if (currentDims.equals(Conf.GRID_SIZES[i])) return i;
        }
        return 0;
    }

    public MainActivityComponent getComponent() {
        return mComponent;
    }

    @Override
    public void updateSavedPictures(List<String> pictures) {
        if (mSavedPicturesView != null && mSavedPicturesView.get() != null) {
            mSavedPicturesView.get().update(pictures);
        }
    }

    @Override
    public void updateCameraPictures(List<String> pictures) {
        if (mCameraPicturesView != null && mCameraPicturesView.get() != null) {
            mCameraPicturesView.get().update(pictures);
        }
    }

    @Override
    public void updateFlickrPictures(List<Photo> photos) {
        if (mFlickrPicturesView != null && mFlickrPicturesView.get() != null) {
            mFlickrPicturesView.get().updatePhotos(photos);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            BasePicturesGridView gridView = null;
            switch (position) {
                case 0:
                    gridView = new SavedPicturesGridView(MainActivity.this);
                    mSavedPicturesView = new WeakReference<SavedPicturesGridView>
                            ((SavedPicturesGridView) gridView);
                    break;
                case 1:
                    gridView = new CameraPicturesGridView(MainActivity.this);
                    mCameraPicturesView = new WeakReference<CameraPicturesGridView>
                            ((CameraPicturesGridView) gridView);
                    break;
                case 2:
                    gridView = new FlickrPicturesGridView(MainActivity.this);
                    mFlickrPicturesView = new WeakReference<FlickrPicturesGridView>
                            ((FlickrPicturesGridView) gridView);
                    break;
            }
            gridView.requestUpdate();
            collection.addView(gridView);
            return gridView;
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
    }
}
