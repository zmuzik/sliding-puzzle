package zmuzik.slidingpuzzle2.mainscreen

import zmuzik.slidingpuzzle2.flickr.Photo

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

interface MainScreenView {
    fun updateSavedPictures(pictures: List<String>)

    fun setWaitingForCameraPictures()

    fun updateCameraPictures(pictures: List<String>)

    fun setWaitingForFlickrPictures()

    fun updateFlickrPictures(photos: List<Photo>)

}
