package zmuzik.slidingpuzzle2.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import zmuzik.slidingpuzzle2.App

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */
@Module
class AppModule(internal var mApplication: Application) {

    @Provides
    @AppContext
    internal fun provideApplicationContext(): Context {
        return mApplication.applicationContext
    }

    @Provides
    internal fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    internal fun provideApp(): App {
        return mApplication as App
    }

    @Provides
    internal fun provideSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}
