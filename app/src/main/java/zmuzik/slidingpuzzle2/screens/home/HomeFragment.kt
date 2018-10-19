package zmuzik.slidingpuzzle2.screens.home

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.screen_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.*
import zmuzik.slidingpuzzle2.repo.model.Picture
import zmuzik.slidingpuzzle2.screens.MainActivity
import java.lang.ref.WeakReference
import java.util.*


class HomeFragment : Fragment(), HomeScreen {

    var searchMenuItem: MenuItem? = null
    var toggleNumbersMenuItem: MenuItem? = null

    var isOpeningGameInProgress = false

    val mainActivity get() = activity as? MainActivity

    private var sharedView: WeakReference<View>? = null

    val viewModel: HomeScreenViewModel by viewModel()
    val prefs: Prefs by inject()

    val screenWidth: Int by lazy {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    val screenHeight: Int by lazy {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

    var cameraTab: CameraPicturesView? = null
        set(value) {
            field = value
            onReadExternalPermission(viewModel.readExternalGrantedLd.value)
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.screen_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = PagesAdapter()
        (toolbar.layoutParams as? ConstraintLayout.LayoutParams)?.topMargin = getStatusBarHeight()
        mainActivity?.setSupportActionBar(toolbar)
        mainActivity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        bottomNav.selectedItemId = R.id.nav_saved
                        searchBar.hide()
                        searchMenuItem?.isVisible = false
                    }
                    1 -> {
                        bottomNav.selectedItemId = R.id.nav_camera
                        fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_photo_camera_24dp))
                        searchBar.hide()
                        searchMenuItem?.isVisible = false
                    }
                    2 -> {
                        bottomNav.selectedItemId = R.id.nav_flickr
                        fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_search_24dp))
                    }
                }
                fab.showIf(position != 0)
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        })
        bottomNav.setOnNavigationItemSelectedListener { item ->
            viewPager.currentItem = when (item.itemId) {
                R.id.nav_saved -> 0
                R.id.nav_camera -> 1
                else -> 2
            }
            return@setOnNavigationItemSelectedListener true
        }

        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) performFlickrSearch()
            true
        }
        searchBar.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) performFlickrSearch()
            true
        }
        searchBar.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) searchBar.hide()
        }

        fab.setOnClickListener {
            when (viewPager.currentItem) {
                1 -> launchCameraApp()
                2 -> showFlickerSearch()
            }
        }
        viewModel.readExternalGrantedLd.observe(this, androidx.lifecycle.Observer { onReadExternalPermission(it) })
        viewModel.pictureUriToOpen.observe(this, androidx.lifecycle.Observer { onGamePicUriRetrieved(it) })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.readExternalGrantedLd.value = isReadExternalGranted()
    }

    fun onReadExternalPermission(granted: Boolean?) {
        if (granted == null) return
        cameraTab?.permissionsComboView?.visibility = if (granted) View.GONE else View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        if (!isReadExternalGranted() && viewModel.prefs.shouldAskReadStoragePerm) requestReadExternalPermission()
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAppPictures()
        viewModel.requestCameraPictures()
        mainActivity?.showStatusBar(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_main, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        searchMenuItem?.isVisible = false
        toggleNumbersMenuItem = menu.findItem(R.id.action_toggle_display_numbers)
        setTileNumbersIcon(prefs.displayTileNumbers)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_search -> performFlickrSearch()
        R.id.action_toggle_display_numbers -> toggleShowNumbers()
        R.id.action_change_grid_size -> openChangeGridSizeDialog()
        else -> super.onOptionsItemSelected(item)
    }

    fun onDataUpdate(tab: BasePicturesView?, resource: Resource<List<Picture>>?) {
        if (tab == null || resource == null) return
        tab.onDataUpdate(resource)
    }

    private fun openChangeGridSizeDialog(): Boolean {
        val ctx = context ?: return true
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(resources.getString(R.string.select_grid_size))
        builder.setSingleChoiceItems(Conf.GRID_SIZES, gridDimsPosition()) { dialog, item ->
            val positionsStr = Conf.GRID_SIZES[item]
            val tokenizer = StringTokenizer(positionsStr, "x")
            val shorterStr = tokenizer.nextToken()
            val longerStr = tokenizer.nextToken()
            viewModel.setGridDimensions(Integer.parseInt(shorterStr), Integer.parseInt(longerStr))
            dialog.dismiss()
            mainActivity?.toast(String.format(resources.getString(R.string.grid_size_selected_to), positionsStr))
        }
        builder.show()
        return true
    }

    fun gridDimsPosition(): Int {
        val currentDims = viewModel.getGridDimensions()
        return Conf.GRID_SIZES.indices.firstOrNull { currentDims == Conf.GRID_SIZES[it] } ?: 0
    }

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun performFlickrSearch(): Boolean {
        mainActivity?.imm?.hideSoftInputFromWindow(searchBar.windowToken, 0)
        main_layout.requestFocus()
        searchBar.hide()
        searchMenuItem?.isVisible = false
        viewModel.requestFlickrSearch(searchBar.text.toString())
        return true
    }

    private fun toggleShowNumbers(): Boolean {
        val onOff = viewModel.toggleShowNumbers()
        setTileNumbersIcon(onOff)
        mainActivity?.toast(if (onOff) R.string.display_tile_numbers_on else R.string.display_tile_numbers_off)
        return true
    }

    fun setTileNumbersIcon(showNumbers: Boolean) = toggleNumbersMenuItem?.setIcon(
            if (showNumbers) R.drawable.ic_tile_with_number else R.drawable.ic_tile_without_number)

    fun showFlickerSearch() {
        searchBar.setText("")
        searchBar.show()
        searchBar.requestFocus()
        searchMenuItem?.isVisible = true
        mainActivity?.imm?.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
    }

    fun launchCameraApp() {
        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val mInfo = context?.packageManager?.resolveActivity(imageCaptureIntent, 0) ?: return
            startActivity(Intent().also {
                it.component = ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name)
                it.action = Intent.ACTION_MAIN
                it.addCategory(Intent.CATEGORY_LAUNCHER)
            })
        } catch (e: Exception) {
            Timber.e("Unable to launch camera")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Keys.REQUEST_PERMISSION_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.prefs.shouldAskReadStoragePerm = false
                viewModel.readExternalGrantedLd.value = true
                viewModel.requestCameraPictures()
            }
        }
    }

    fun isReadExternalGranted(): Boolean {
        val ctx = context ?: return false
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestReadExternalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Keys.REQUEST_PERMISSION_READ_STORAGE)
        }
    }

    override fun runGame(itemView: View, tab: PictureTab, position: Int): Boolean {
        if (isOpeningGameInProgress) return false
        isOpeningGameInProgress = true
        sharedView = WeakReference(itemView)
        viewModel.runGame(tab, position, Math.max(screenHeight, screenWidth))
        return true
    }

    private fun onGamePicUriRetrieved(uri: String?) {
        safeLet(mainActivity, uri, sharedView?.get()) { lactivity, luri, lview ->
            findNavController().navigate(R.id.gameFragment, bundleOf("PICTURE_URI" to luri))
            isOpeningGameInProgress = false
        }
    }

    private inner class PagesAdapter : PagerAdapter() {

        override fun instantiateItem(views: ViewGroup, position: Int): Any {
            val (tab, liveData) = when (position) {
                0 -> Pair(AppPicturesView(views.context), viewModel.repo.appPicturesLd).also { it.first.homeScreen = this@HomeFragment }
                1 -> Pair(CameraPicturesView(views.context), viewModel.repo.cameraPicturesLd).also { it.first.homeScreen = this@HomeFragment; cameraTab = it.first }
                else -> Pair(FlickrPicturesView(views.context), viewModel.repo.flickrPicturesLd).also { it.first.homeScreen = this@HomeFragment }
            }
            liveData.observe(this@HomeFragment, androidx.lifecycle.Observer { onDataUpdate(tab, it) })
            views.addView(tab)
            return tab
        }

        override fun destroyItem(views: ViewGroup, pos: Int, view: Any) {
            val liveData = when (pos) {
                0 -> viewModel.repo.appPicturesLd
                1 -> {
                    cameraTab = null
                    viewModel.repo.cameraPicturesLd
                }
                else -> viewModel.repo.flickrPicturesLd
            }
            liveData.removeObservers(this@HomeFragment)
            views.removeView(view as View)
        }

        override fun getCount() = 3

        override fun isViewFromObject(view: View, obj: Any) = view === obj
    }
}
