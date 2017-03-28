package zmuzik.slidingpuzzle2.flickr;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import zmuzik.slidingpuzzle2.BuildConfig;
import zmuzik.slidingpuzzle2.Conf;

public interface FlickrApi {

    public static final String FLICKR_API_ROOT = "https://api.flickr.com/services/rest/";

    @GET("?method=flickr.photos.search" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_l,o_dims,url_c,url_o" +
            "&sort=interestingness-desc" +
            "&per_page=" + Conf.FLICKR_REQUEST_IMAGES +
            "&api_key=" + BuildConfig.FLICKR_API_KEY)
    Call<SearchResponse> getPhotos(@Query("text") String query);

    @GET("?method=flickr.photos.getSizes" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&api_key=" + BuildConfig.FLICKR_API_KEY)
    Call<PhotoSizesResponse> getSizes(@Query("photo_id") String id);


}
