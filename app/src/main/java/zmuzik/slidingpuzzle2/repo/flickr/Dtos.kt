package zmuzik.slidingpuzzle2.repo.flickr


/**
 * Data transfer objects related to the Flickr API
 *
 * Constants representing picture sizes in the API:
 * s	small square 75x75
 * q	large square 150x150
 * t	thumbnail, 100 on longest side
 * m	small, 240 on longest side
 * n	small, 320 on longest side
 * -	medium, 500 on longest side
 * z	medium 640, 640 on longest side
 * c	medium 800, 800 on longest side†
 * b	large, 1024 on longest side*
 * h	large 1600, 1600 on longest side†
 * k	large 2048, 2048 on longest side†
 * o	original image, either a jpg, gif or png, depending on source format
 */

data class FlickrPhoto(
        var id: String = "",
        var secret: String? = null,
        var server: String? = null,
        var farm: Int = 0,
        var title: String? = null,
        var height_l: Int = 0,
        var width_l: Int = 0,
        var url_c: String? = null,
        var url_o: String? = null
) {

    val thumbUrl: String
        get() = "https://farm$farm.staticflickr.com/$server/${id}_${secret}_q.jpg"

    fun getFullPicUrl(maxScreenDim: Int, flickrPhotoSizes: List<FlickrPhotoSize>): String {
        var result = thumbUrl
        val prevSize = 0
        val sortedSizes = flickrPhotoSizes.sortedBy { it.maxDim }
        sortedSizes
                .filter { it.maxDim in (prevSize + 1)..maxScreenDim }
                .forEach { result = it.source }
        return result
    }

    val isHorizontal: Boolean get() = width_l > height_l
}

data class FlickrPhotos(val photo: List<FlickrPhoto>? = null)

data class FlickrSearchResponse(val photos: FlickrPhotos? = null)

data class FlickrPhotoSize(
        var width: Int = 0,
        var height: Int = 0,
        var source: String = ""
) {
    val maxDim: Int
        get() = if (width > height) width else height
}

data class FlickrPhotoSizes(var size: List<FlickrPhotoSize>? = null)

data class FlickrPhotoSizesResponse(val sizes: FlickrPhotoSizes? = null)
