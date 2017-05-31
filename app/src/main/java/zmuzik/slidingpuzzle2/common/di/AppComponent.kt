package zmuzik.slidingpuzzle2.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import javax.inject.Singleton

import dagger.Component
import zmuzik.slidingpuzzle2.App
import zmuzik.slidingpuzzle2.common.PreferencesHelper
import zmuzik.slidingpuzzle2.flickr.FlickrApi
import zmuzik.slidingpuzzle2.gamescreen.PuzzleBoardView
import zmuzik.slidingpuzzle2.mainscreen.GetFlickrPicsPageTask

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface AppComponent {

    fun inject(helper: PreferencesHelper)

    fun inject(task: GetFlickrPicsPageTask)

    @get:AppContext
    val applicationContext: Context

    val application: Application

    val app: App

    val sharedPreferences: SharedPreferences

    val prefsHelper: PreferencesHelper

    val flickrApi: FlickrApi
}