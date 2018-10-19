package zmuzik.slidingpuzzle2

import android.preference.PreferenceManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zmuzik.slidingpuzzle2.common.Prefs
import zmuzik.slidingpuzzle2.common.ShakeDetector
import zmuzik.slidingpuzzle2.repo.Repo
import zmuzik.slidingpuzzle2.repo.flickr.FlickrApi
import zmuzik.slidingpuzzle2.screens.game.GameScreenViewModel
import zmuzik.slidingpuzzle2.screens.home.HomeScreenViewModel

val appModule: Module = module {

    single {
        Retrofit.Builder()
                .baseUrl(FlickrApi.FLICKR_API_ROOT)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FlickrApi::class.java)
    }

    single {
        Prefs(PreferenceManager.getDefaultSharedPreferences(get()))
    }

    single { Repo(get()) }

    single { ShakeDetector(get()) }

    viewModel { HomeScreenViewModel(get(), get()) }

    viewModel { GameScreenViewModel(get(), get()) }
}
