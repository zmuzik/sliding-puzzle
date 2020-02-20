package zmuzik.slidingpuzzle2.repo.flickr

import retrofit2.http.GET
import retrofit2.http.Query
import zmuzik.slidingpuzzle2.BuildConfig
import zmuzik.slidingpuzzle2.Conf

interface FlickrApi {

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

    companion object {
        val FLICKR_API_ROOT = "https://api.flickr.com/services/rest/"
    }
}
