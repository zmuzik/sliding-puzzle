package zmuzik.slidingpuzzle2.common.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import zmuzik.slidingpuzzle2.App;
import zmuzik.slidingpuzzle2.common.PreferencesHelper;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.gamescreen.PuzzleBoardView;

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                NetModule.class
        }
)
public interface AppComponent {

    void inject(PuzzleBoardView view);

    void inject(PreferencesHelper helper);

    Context getContext();

    Application getApplication();

    App getApp();

    SharedPreferences getSharedPreferences();

    PreferencesHelper getPrefsHelper();

    FlickrApi getFlickrApi();
}