package zmuzik.slidingpuzzle2.mainscreen

import zmuzik.slidingpuzzle2.flickr.Photo

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

interface MainScreenView {
    fun updateSavedPictures(strings: List<String>)

    fun setWaitingForCameraPictures()

    fun updateCameraPictures(strings: List<String>)

    fun setWaitingForFlickrPictures()

    fun updateFlickrPictures(strings: List<Photo>)

}
