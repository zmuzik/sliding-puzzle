package zmuzik.slidingpuzzle2.ui.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.StringTokenizer;

import javax.inject.Inject;

import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.R;
import zmuzik.slidingpuzzle2.adapters.MainScreenPagerAdapter;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;
import zmuzik.slidingpuzzle2.view.SavedPicturesGridView;


public class MainActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();

    final String[] GRID_SIZES = {
            "3x3", "3x4", "3x5", "3x6",
            "4x4", "4x5", "4x6",
            "5x5", "5x6", "6x6",};

    ViewPager mViewPager;

    @Inject
    PrefsHelper mPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent().inject(this);
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

        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {
            mViewPager.setAdapter(new MainScreenPagerAdapter(this));
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void changeGridSize() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int prevPosition = mPrefsHelper.getGridDimsPosition();
        builder.setTitle(getString(R.string.select_grid_size));
        builder.setSingleChoiceItems(GRID_SIZES, prevPosition, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String positionsStr = GRID_SIZES[item];
                StringTokenizer tokenizer = new StringTokenizer(positionsStr, "x");
                String shorterStr = tokenizer.nextToken();
                String longerStr = tokenizer.nextToken();
                mPrefsHelper.setGridDimsPosition(item);
                mPrefsHelper.setGridDimShort(Integer.parseInt(shorterStr));
                mPrefsHelper.setGridDimLong(Integer.parseInt(longerStr));
                dialog.dismiss();
                String msg = String.format(App.get().getString(R.string.grid_size_selected_to), positionsStr);
                Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void toggleShowNumbers() {
        boolean value = mPrefsHelper.getDisplayTileNumbers();
        value = !value;
        mPrefsHelper.setDisplayTileNumbers(value);
        String msg = getString(value ? R.string.display_tile_numbers_on : R.string.display_tile_numbers_off);
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
    }
}
