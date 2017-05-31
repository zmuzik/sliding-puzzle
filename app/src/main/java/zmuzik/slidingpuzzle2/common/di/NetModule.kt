package zmuzik.slidingpuzzle2.common.di

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.flickr.FlickrApi

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

@Module
class NetModule {

    @Provides
    @Singleton
    internal fun provideFlickrApi(): FlickrApi {
        val retrofit = Retrofit.Builder()
                .baseUrl(FlickrApi.FLICKR_API_ROOT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(FlickrApi::class.java)
    }
}
