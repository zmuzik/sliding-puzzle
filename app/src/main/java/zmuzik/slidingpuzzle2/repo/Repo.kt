package zmuzik.slidingpuzzle2.repo

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import timber.log.Timber
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.common.FILE_PREFIX
import zmuzik.slidingpuzzle2.common.FileContainer
import zmuzik.slidingpuzzle2.common.Resource
import zmuzik.slidingpuzzle2.common.isPicture
import zmuzik.slidingpuzzle2.repo.flickr.FlickrApi
import zmuzik.slidingpuzzle2.repo.model.Picture
import java.io.File
import kotlin.coroutines.CoroutineContext

class Repo(val flickrApi: FlickrApi) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val appPictures = mutableListOf<Picture.LocalPicture>()
    val cameraPictures = mutableListOf<Picture.LocalPicture>()
    val flickrPictures = mutableListOf<Picture.FlickrPicture>()

    val appPicturesLd = MutableLiveData<Resource<List<Picture>>>()
    val cameraPicturesLd = MutableLiveData<Resource<List<Picture>>>()
    val flickrPicturesLd = MutableLiveData<Resource<List<Picture>>>()

    fun updateAppPictures() {
        appPictures.clear()
        appPictures.addAll(Conf.APP_PHOTOS.asList().map { Picture.LocalPicture(it) })
        appPicturesLd.value = Resource.Success(appPictures)
    }

    fun updateCameraPictures() = launch {
        cameraPicturesLd.value = Resource.Loading()
        val foundFiles = getCameraPictures().await()
        cameraPictures.clear()
        cameraPictures.addAll(foundFiles.map { Picture.LocalPicture(it.filePath) })
        cameraPicturesLd.value = Resource.Success(cameraPictures)
    }

    fun updateFlickrPictures(query: String) = launch {
        flickrPicturesLd.value = Resource.Loading()
        val response = flickrApi.getPhotos(query).await()
        if (response.isSuccessful) {
            val resultList = response.body()?.photos?.photo?: emptyList()
            flickrPictures.clear()
            flickrPictures.addAll(resultList.map { Picture.FlickrPicture(it) })
            flickrPicturesLd.value = Resource.Success(flickrPictures)
        } else {
            Timber.e(response.message())
            flickrPicturesLd.value = Resource.Failure(response.code())
        }
    }

    fun getCameraPictures(): Deferred<List<FileContainer>> = async {
        val foundFiles = mutableListOf<FileContainer>()
        val cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        scanDirectoryForPictures(cameraDir, foundFiles)
        foundFiles.also { it.sortByDescending { it.lastModified } }
    }

    private fun scanDirectoryForPictures(root: File?, filePaths: MutableList<FileContainer>) {
        if (root == null) return
        val list = root.listFiles() ?: return
        list.filterNot { it.isHidden }.forEach {
            if (it.isDirectory) {
                scanDirectoryForPictures(it, filePaths)
            } else if (isPicture(it)) {
                filePaths.add(FileContainer(FILE_PREFIX + it.absolutePath, it.lastModified()))
            }
        }
    }

    fun getPhotoSizes(photoId: String) = flickrApi.getPhotoSizes(photoId)
}