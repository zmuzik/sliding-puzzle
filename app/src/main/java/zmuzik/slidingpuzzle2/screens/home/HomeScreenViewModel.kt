package zmuzik.slidingpuzzle2.screens.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import zmuzik.slidingpuzzle2.common.PictureTab
import zmuzik.slidingpuzzle2.common.Prefs
import zmuzik.slidingpuzzle2.common.SingleLiveEvent
import zmuzik.slidingpuzzle2.repo.Repo
import kotlin.math.max

class HomeScreenViewModel(val repo: Repo, val prefs: Prefs) : ViewModel() {

    val readExternalGrantedLd = MutableLiveData<Boolean>().also { it.value = false }

    val pictureUriToOpen = SingleLiveEvent<String>()

    fun runGame(tab: PictureTab, position: Int, maxDim: Int) {
        when (tab) {
            PictureTab.APP -> pictureUriToOpen.value = repo.appPictures[position].url
            PictureTab.CAMERA -> pictureUriToOpen.value = repo.cameraPictures[position].url
            PictureTab.FLICKR -> viewModelScope.launch {
                val photo = repo.flickrPictures[position]
                try {
                    val response = repo.getFlickrPhotoSizes(photo.id)
                    val picture = response.sizes?.size?.find { max(it.height, it.width) >= maxDim }
                            ?: response.sizes?.size?.last()
                    pictureUriToOpen.postValue(picture?.source)
                } catch (t: Throwable) {
                    Timber.e(t)
                    pictureUriToOpen.postValue(null)
                }
            }
        }
    }

    fun requestAppPictures() = repo.updateAppPictures()

    fun requestCameraPictures() = viewModelScope.launch {
        repo.updateCameraPictures()
    }

    fun requestFlickrSearch(searchQuery: String) = viewModelScope.launch {
        repo.updateFlickrPictures(searchQuery)
    }

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
