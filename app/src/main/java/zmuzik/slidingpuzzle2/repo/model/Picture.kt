package zmuzik.slidingpuzzle2.repo.model

import zmuzik.slidingpuzzle2.repo.flickr.FlickrPhoto


sealed class Picture(val thumbUrl: String) {

    open var isHorizontal: Boolean? = null

    open var url: String? = thumbUrl

    class LocalPicture(thumbUrl: String) : Picture(thumbUrl)

    class FlickrPicture(val flickrPhoto: FlickrPhoto) : Picture(flickrPhoto.thumbUrl) {
        override var url: String? = null
        val id: String get() = flickrPhoto.id
    }
}
