package zmuzik.slidingpuzzle.flickr;

import retrofit.http.GET;
import retrofit.http.Query;
import zmuzik.slidingpuzzle.Conf;

public interface FlickrApi {

    @GET("/?method=flickr.photos.search" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_l" +
            "&per_page=12"
            + "&api_key=" + Conf.FLICKR_API_KEY) SearchResponse getPhotos(@Query("text") String query);

}
