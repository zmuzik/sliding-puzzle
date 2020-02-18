package zmuzik.slidingpuzzle2.repo.flickr

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import zmuzik.slidingpuzzle2.BuildConfig
import zmuzik.slidingpuzzle2.Conf

interface FlickrApi {

    companion object {
        val FLICKR_API_ROOT = "https://api.flickr.com/services/rest/"
    }

    @GET("?method=flickr.photos.search" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_l,o_dims,url_c,url_o" +
            "&sort=interestingness-desc" +
            "&per_page=" + Conf.FLICKR_REQUEST_IMAGES +
            "&api_key=" + BuildConfig.FLICKR_API_KEY)
    suspend fun getPhotos(@Query("text") query: String): FlickrSearchResponse

    @GET("?method=flickr.photos.getSizes" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&api_key=" + BuildConfig.FLICKR_API_KEY)
    suspend fun getPhotoSizes(@Query("photo_id") photoId: String): FlickrPhotoSizesResponse
}

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
//    s	small square 75x75
//    q	large square 150x150
//    t	thumbnail, 100 on longest side
//    m	small, 240 on longest side
//    n	small, 320 on longest side
//    -	medium, 500 on longest side
//    z	medium 640, 640 on longest side
//    c	medium 800, 800 on longest side†
//    b	large, 1024 on longest side*
//    h	large 1600, 1600 on longest side†
//    k	large 2048, 2048 on longest side†
//    o	original image, either a jpg, gif or png, depending on source format

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


class FlickrPhotoSize(
        var width: Int = 0,
        var height: Int = 0,
        var source: String = ""
) {
    val maxDim: Int
        get() = if (width > height) width else height
}

data class FlickrPhotoSizes(var size: List<FlickrPhotoSize>? = null)

data class FlickrPhotoSizesResponse(val sizes: FlickrPhotoSizes? = null)

