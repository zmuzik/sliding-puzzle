package zmuzik.slidingpuzzle2.ui.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.StringTokenizer;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;
import zmuzik.slidingpuzzle2.ui.fragments.CameraPicturesFragment;
import zmuzik.slidingpuzzle2.ui.fragments.FlickrPicturesFragment;
import zmuzik.slidingpuzzle2.ui.fragments.SavedPicturesFragment;


public class MainActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();

    final String[] GRID_SIZES = {
            "3x3", "3x4", "3x5", "3x6",
            "4x4", "4x5", "4x6",
            "5x5", "5x6", "6x6",};

    PagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(null);
            actionBar.setSubtitle(null);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section1)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section2)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_section3)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mSectionsPagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                Crashlytics.log(Log.DEBUG, TAG, "onTabSelected " + tab.getPosition());
                Crashlytics.setInt("tab", tab.getPosition());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_grid_size) {
            changeGridSize();
            return true;
        }
        if (id == R.id.action_toggle_display_numbers) {
            toggleShowNumbers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Crashlytics.log(Log.DEBUG, TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Crashlytics.log(Log.DEBUG, TAG, "onResume");
        Crashlytics.setString("screen", TAG);
    }

    public void changeGridSize() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int prevPosition = PrefsHelper.get().getGridDimsPosition();
        builder.setTitle(getString(R.string.select_grid_size));
        builder.setSingleChoiceItems(GRID_SIZES, prevPosition, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String positionsStr = GRID_SIZES[item];
                StringTokenizer tokenizer = new StringTokenizer(positionsStr, "x");
                String shorterStr = tokenizer.nextToken();
                String longerStr = tokenizer.nextToken();
                PrefsHelper.get().setGridDimsPosition(item);
                PrefsHelper.get().setGridDimShort(Integer.parseInt(shorterStr));
                PrefsHelper.get().setGridDimLong(Integer.parseInt(longerStr));
                dialog.dismiss();
                Toast.makeText(App.get(), App.get().getString(R.string.grid_size_selected_to) + positionsStr,
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void toggleShowNumbers() {
        boolean value = PrefsHelper.get().getDisplayTileNumbers();
        value = !value;
        PrefsHelper.get().setDisplayTileNumbers(value);
        String msg = getString(value ? R.string.display_tile_numbers_on : R.string.display_tile_numbers_off);
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SavedPicturesFragment();
                case 1:
                    return new CameraPicturesFragment();
                case 2:
                    return new FlickrPicturesFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
