package zmuzik.slidingpuzzle2.repo

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.common.FILE_PREFIX
import zmuzik.slidingpuzzle2.common.FileWrapper
import zmuzik.slidingpuzzle2.common.Resource
import zmuzik.slidingpuzzle2.common.isPicture
import zmuzik.slidingpuzzle2.repo.flickr.FlickrApi
import zmuzik.slidingpuzzle2.repo.model.Picture
import java.io.File

class Repo(private val flickrApi: FlickrApi) {

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

    suspend fun updateCameraPictures() {
        cameraPicturesLd.value = Resource.Loading()
        val foundFiles = withContext(Dispatchers.IO) {
            retrieveCameraPictures()
        }
        cameraPictures.clear()
        cameraPictures.addAll(foundFiles.map { Picture.LocalPicture(it.filePath) })
        cameraPicturesLd.value = Resource.Success(cameraPictures)
    }

    suspend fun updateFlickrPictures(query: String) {
        flickrPicturesLd.value = Resource.Loading()
        try {
            val response = flickrApi.getPhotos(query)
            val resultList = response.photos?.photo ?: emptyList()
            flickrPictures.clear()
            flickrPictures.addAll(resultList.map { Picture.FlickrPicture(it) })
            flickrPicturesLd.value = Resource.Success(flickrPictures)
        } catch (t: Throwable) {
            Timber.e(t)
            flickrPicturesLd.value = Resource.Failure()
        }
    }

    suspend fun getFlickrPhotoSizes(photoId: String) = flickrApi.getPhotoSizes(photoId)

    private fun retrieveCameraPictures(): List<FileWrapper> {
        val foundFiles = mutableListOf<FileWrapper>()
        val cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        scanDirectoryForPictures(cameraDir, foundFiles)
        foundFiles.also { it.sortByDescending { it.lastModified } }
        return foundFiles
    }

    private fun scanDirectoryForPictures(root: File?, filePaths: MutableList<FileWrapper>) {
        if (root == null) return
        val list = root.listFiles() ?: return
        list.filterNot { it.isHidden }.forEach {
            if (it.isDirectory) {
                scanDirectoryForPictures(it, filePaths)
            } else if (isPicture(it)) {
                filePaths.add(FileWrapper(FILE_PREFIX + it.absolutePath, it.lastModified()))
            }
        }
    }
}
