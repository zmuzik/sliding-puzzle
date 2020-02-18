package zmuzik.slidingpuzzle2.screens.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import zmuzik.slidingpuzzle2.common.PictureTab
import zmuzik.slidingpuzzle2.common.Prefs
import zmuzik.slidingpuzzle2.common.SingleLiveEvent
import zmuzik.slidingpuzzle2.repo.Repo

class HomeScreenViewModel(val repo: Repo, val prefs: Prefs) : ViewModel() {

    val readExternalGrantedLd = MutableLiveData<Boolean>().also { it.value = false }

    val pictureUriToOpen = SingleLiveEvent<String>()

    fun runGame(tab: PictureTab, position: Int, maxDim: Int) {
        when (tab) {
            PictureTab.APP -> pictureUriToOpen.value = repo.appPictures[position].url
            PictureTab.CAMERA -> pictureUriToOpen.value = repo.cameraPictures[position].url
            PictureTab.FLICKR -> GlobalScope.launch {
                val photo = repo.flickrPictures[position]
                val response = repo.getPhotoSizes(photo.id).await()
                if (response.isSuccessful) {
                    val picture = response.body()?.sizes?.size?.find { Math.max(it.height, it.width) >= maxDim }
                            ?: response.body()?.sizes?.size?.last()
                    pictureUriToOpen.postValue(picture?.source)
                } else {
                    Timber.e(response.message())
                    pictureUriToOpen.postValue(null)
                }
            }
        }
    }

    fun requestAppPictures() = repo.updateAppPictures()

    fun requestCameraPictures() = repo.updateCameraPictures()

    fun requestFlickrSearch(searchQuery: String) = repo.updateFlickrPictures(searchQuery)

    fun toggleShowNumbers(): Boolean {
        prefs.displayTileNumbers = !prefs.displayTileNumbers
        return prefs.displayTileNumbers
    }

    fun getGridDimensions() = "" + prefs.gridDimShort + "x" + prefs.gridDimLong

    fun setGridDimensions(gridDimShort: Int, gridDimLong: Int) {
        prefs.gridDimShort = gridDimShort
        prefs.gridDimLong = gridDimLong
    }
}
