package zmuzik.slidingpuzzle.flickr;

import retrofit.http.GET;
import retrofit.http.Query;
import zmuzik.slidingpuzzle.Conf;

public interface FlickrApi {

    @GET("/?method=flickr.photos.search" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_l,o_dims,url_c,url_o" +
            "&sort=interestingness-desc" +
            "&per_page=" + Conf.FLICKR_REQUEST_IMAGES +
            "&api_key=" + Conf.FLICKR_API_KEY) SearchResponse getPhotos(@Query("text") String query);

    @GET("/?method=flickr.photos.getSizes" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&api_key=" + Conf.FLICKR_API_KEY) PhotoSizesResponse getSizes(@Query("photo_id") String id);


}
