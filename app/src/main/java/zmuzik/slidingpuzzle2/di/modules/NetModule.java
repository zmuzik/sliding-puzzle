package zmuzik.slidingpuzzle2.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zmuzik.slidingpuzzle2.Conf;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

@Module
public class NetModule {

    @Provides
    @Singleton
    FlickrApi provideFlickrApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Conf.FLICKR_API_ROOT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(FlickrApi.class);
    }
}
