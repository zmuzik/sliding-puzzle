package zmuzik.slidingpuzzle2.mainscreen

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.gson.Gson
import zmuzik.slidingpuzzle2.ASSET_PREFIX
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.Keys
import zmuzik.slidingpuzzle2.common.PreferencesHelper
import zmuzik.slidingpuzzle2.common.Toaster
import zmuzik.slidingpuzzle2.common.di.ActivityContext
import zmuzik.slidingpuzzle2.common.di.ActivityScope
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.flickr.Photo
import zmuzik.slidingpuzzle2.gamescreen.GameActivity
import zmuzik.slidingpuzzle2.isOnline
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
class MainScreenPresenter @Inject
constructor() {

    private val TAG = this.javaClass.simpleName

    val REQUEST_PERMISSION_READ_STORAGE = 101

    private val SAVED_PICTURES = arrayOf(
            ASSET_PREFIX + "game_pic_00.jpg",
            ASSET_PREFIX + "game_pic_01.jpg",
            ASSET_PREFIX + "game_pic_07.jpg",
            ASSET_PREFIX + "game_pic_02.jpg",
            ASSET_PREFIX + "game_pic_03.jpg",
            ASSET_PREFIX + "game_pic_04.jpg",
            ASSET_PREFIX + "game_pic_05.jpg",
            ASSET_PREFIX + "game_pic_06.jpg",
            ASSET_PREFIX + "game_pic_08.jpg",
            ASSET_PREFIX + "game_pic_09.jpg",
            ASSET_PREFIX + "game_pic_10.jpg",
            ASSET_PREFIX + "game_pic_11.jpg")

    @Inject
    @ActivityContext
    lateinit var context: Context
    @Inject
    lateinit var prefsHelper: PreferencesHelper
    @Inject
    lateinit var flickrApi: FlickrApi
    @Inject
    lateinit var toaster: Toaster
    @Inject
    lateinit var view: MainScreenView

    private var isCameraPicturesUpdating: Boolean = false
    private var flickerPictures: List<Photo> = ArrayList()

    internal fun onResume() {
        if (view.isCameraTabActive()) requestUpdateCameraPictures()
    }

    internal fun onPause() {}

    internal fun toggleShowNumbers(): Boolean {
        prefsHelper.displayTileNumbers = !prefsHelper.displayTileNumbers
        return prefsHelper.displayTileNumbers
    }

    fun isShowTileNumbers() = prefsHelper.displayTileNumbers

    fun getGridDimensions() = "" + prefsHelper.gridDimShort + "x" + prefsHelper.gridDimLong

    internal fun setGridDimensions(gridDimShort: Int, gridDimLong: Int) {
        prefsHelper.gridDimShort = gridDimShort
        prefsHelper.gridDimLong = gridDimLong
    }

    internal fun runGame(pictureUri: String, isHorizontal: Boolean) {
        val intent = Intent(context, GameActivity::class.java)
        intent.putExtra(Keys.PICTURE_URI, pictureUri)
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal)
        context.startActivity(intent)
    }

    internal fun runGame(photo: Photo?) {
        val isHorizontal = photo != null && photo.width_l > photo.height_l
        val intent = Intent(context, GameActivity::class.java)
        val photoStr = Gson().toJson(photo)
        intent.putExtra(Keys.PHOTO, photoStr)
        intent.putExtra(Keys.IS_HORIZONTAL, isHorizontal)
        context.startActivity(intent)
    }

    //***SavedPicturesGridView***

    internal fun requestUpdateSavedPictures() {
        updateSavedPictures()
    }

    internal fun updateSavedPictures() {
        view.updateSavedPictures(Arrays.asList(*SAVED_PICTURES))
    }

    //***CameraPicturesGridView***

    internal fun requestUpdateCameraPictures() {
        if (!isReadExternalGranted && prefsHelper.shouldAskReadStoragePerm) {
            requestReadExternalPermission()
        } else if (!isCameraPicturesUpdating) {
            isCameraPicturesUpdating = true
            view.setWaitingForCameraPictures()
            UpdateCameraFilesTask(this).execute()
        }
    }

    internal fun updateCameraPictures(pictures: List<String>) {
        isCameraPicturesUpdating = false
        view.updateCameraPictures(pictures)
    }

    internal val isReadExternalGranted: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            } else {
                return true
            }
        }

    internal fun requestReadExternalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as MainActivity).requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_READ_STORAGE)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            prefsHelper.shouldAskReadStoragePerm = false
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUpdateCameraPictures()
            }
        }
    }

    fun launchCameraApp() {
        val auxIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val pm = context.packageManager
            val mInfo = pm.resolveActivity(auxIntent, 0)
            val intent = Intent()
            intent.component = ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.i(TAG, "Unable to launch camera: " + e)
        }

    }

    //***FlickrPicturesGridView***

    fun requestFlickrSearch(keywords: String?) {
        if (!isOnline(context)) {
            toaster.show(R.string.internet_unavailable)
            return
        }

        if (keywords == null || "" == keywords) {
            toaster.show(R.string.keyword_not_supplied)
            return
        }
        view.setWaitingForFlickrPictures()
        GetFlickrPicsPageTask(keywords, this, flickrApi).execute()
    }

    internal fun requestUpdateFlickrPictures() {
        updateFlickrPictures(flickerPictures)
    }

    internal fun updateFlickrPictures(photos: List<Photo>) {
        flickerPictures = photos
        view.updateFlickrPictures(flickerPictures)
    }
}
