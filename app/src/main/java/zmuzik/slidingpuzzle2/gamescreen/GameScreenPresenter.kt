package zmuzik.slidingpuzzle2.gamescreen

import android.content.Context
import android.content.Intent

import com.google.gson.Gson

import javax.inject.Inject

import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.Keys
import zmuzik.slidingpuzzle2.common.PreferencesHelper
import zmuzik.slidingpuzzle2.common.Toaster
import zmuzik.slidingpuzzle2.common.di.ActivityContext
import zmuzik.slidingpuzzle2.common.di.ActivityScope
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.flickr.Photo
import zmuzik.slidingpuzzle2.isOnline

/**
 * Created by Zbynek Muzik on 2017-04-03.
 */

@ActivityScope
class GameScreenPresenter @Inject
constructor() {

    private val TAG = this.javaClass.simpleName

    @Inject
    lateinit var mPrefsHelper: PreferencesHelper
    @Inject
    lateinit var mFlickrApi: FlickrApi
    @Inject
    lateinit var mToaster: Toaster
    @Inject
    lateinit var mView: GameScreenView
    @Inject
    @ActivityContext
    lateinit var mContext: Context

    lateinit var mPictureUri: String

    fun requestPictureUri(intent: Intent) {
        val uri = intent.extras.getString(Keys.PICTURE_URI)
        if (uri != null) {
            loadPictureUri(uri)
        } else {
            val photoStr = intent.extras.getString(Keys.PHOTO)
            val photo = Gson().fromJson(photoStr, Photo::class.java)
            if (isOnline(mContext)) {
                GetFlickrPhotoSizesTask(photo, this, mFlickrApi).execute()
            } else {
                mView.finishWithMessage(R.string.internet_unavailable)
            }
        }
    }

    fun loadPictureUri(uri: String) {
        mPictureUri = uri
        mView.loadPicture(mPictureUri)
    }

    fun finishWithMessage(stringId: Int) {
        mView.finishWithMessage(stringId)
    }

    fun getMaxScreenDim() = mView.getMaxScreenDim()
}
