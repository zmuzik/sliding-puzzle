package zmuzik.slidingpuzzle2.mainscreen


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_main.*
import zmuzik.slidingpuzzle2.App
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.Toaster
import zmuzik.slidingpuzzle2.common.di.ActivityScope
import zmuzik.slidingpuzzle2.flickr.Photo
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@ActivityScope
class MainActivity : AppCompatActivity(), MainScreenView {

    internal val TAG = this.javaClass.simpleName

    lateinit var component: MainActivityComponent
    lateinit var toggleNumbersMenuItem: MenuItem

    internal var savedPicturesView: WeakReference<SavedPicturesGridView>? = null
    internal var cameraPicturesView: WeakReference<CameraPicturesGridView>? = null
    internal var flickrPicturesView: WeakReference<FlickrPicturesGridView>? = null

    @Inject
    lateinit var presenter: MainScreenPresenter
    @Inject
    lateinit var toaster: Toaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewPager.adapter = ViewPagerAdapter()
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun inject() {
        component = DaggerMainActivityComponent.builder()
                .appComponent((application as App).getComponent(this))
                .mainActivityModule(MainActivityModule(this))
                .build()
        component.inject(this)
        component.inject(presenter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        toggleNumbersMenuItem = menu.findItem(R.id.action_toggle_display_numbers)
        setTileNumbersIcon(presenter.isShowTileNumbers())
        return true
    }

    internal fun setTileNumbersIcon(showNumbers: Boolean) {
        toggleNumbersMenuItem.setIcon(if (showNumbers)
            R.drawable.ic_tile_with_number
        else
            R.drawable.ic_tile_without_number)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_grid_size -> {
                openChangeGridSizeDialog()
                return true
            }
            R.id.action_toggle_display_numbers -> {
                toggleShowNumbers()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleShowNumbers() {
        val onOff = presenter!!.toggleShowNumbers()
        setTileNumbersIcon(onOff)
        toaster.show(if (onOff) R.string.display_tile_numbers_on else R.string.display_tile_numbers_off)
    }

    private fun openChangeGridSizeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.select_grid_size))
        builder.setSingleChoiceItems(Conf.GRID_SIZES, gridDimsPosition) { dialog, item ->
            val positionsStr = Conf.GRID_SIZES[item]
            val tokenizer = StringTokenizer(positionsStr, "x")
            val shorterStr = tokenizer.nextToken()
            val longerStr = tokenizer.nextToken()
            presenter.setGridDimensions(Integer.parseInt(shorterStr), Integer.parseInt(longerStr))
            dialog.dismiss()
            toaster.show(String.format(resources.getString(R.string.grid_size_selected_to), positionsStr))
        }
        builder.show()
    }

    private val gridDimsPosition: Int
        get() {
            val currentDims = presenter.getGridDimensions()
            for (i in Conf.GRID_SIZES.indices) {
                if (currentDims == Conf.GRID_SIZES[i]) return i
            }
            return 0
        }

    override fun updateSavedPictures(pictures: List<String>) {
        if (savedPicturesView != null && savedPicturesView!!.get() != null) {
            savedPicturesView!!.get()?.update(pictures)
        }
    }

    override fun setWaitingForCameraPictures() {
        if (cameraPicturesView != null && cameraPicturesView!!.get() != null) {
            cameraPicturesView!!.get()?.setWaitingForPictures()
        }
    }

    override fun updateCameraPictures(pictures: List<String>) {
        if (cameraPicturesView != null && cameraPicturesView!!.get() != null) {
            cameraPicturesView!!.get()?.update(pictures)
        }
    }

    override fun setWaitingForFlickrPictures() {
        if (flickrPicturesView != null && flickrPicturesView!!.get() != null) {
            flickrPicturesView!!.get()?.setWaitingForPictures()
        }
    }

    override fun updateFlickrPictures(photos: List<Photo>) {
        if (flickrPicturesView != null && flickrPicturesView!!.get() != null) {
            flickrPicturesView!!.get()?.updatePhotos(photos)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        presenter!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private inner class ViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            var gridView: BasePicturesGridView? = null
            when (position) {
                0 -> {
                    gridView = SavedPicturesGridView(this@MainActivity)
                    savedPicturesView = WeakReference<SavedPicturesGridView>(gridView as SavedPicturesGridView?)
                }
                1 -> {
                    gridView = CameraPicturesGridView(this@MainActivity)
                    cameraPicturesView = WeakReference<CameraPicturesGridView>(gridView as CameraPicturesGridView?)
                }
                2 -> {
                    gridView = FlickrPicturesGridView(this@MainActivity)
                    flickrPicturesView = WeakReference<FlickrPicturesGridView>(gridView as FlickrPicturesGridView?)
                }
            }
            gridView!!.requestUpdate()
            collection.addView(gridView)
            return gridView
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return 3
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}
