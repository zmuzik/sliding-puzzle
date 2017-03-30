package zmuzik.slidingpuzzle2.di.components;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import zmuzik.slidingpuzzle2.di.modules.AppModule;
import zmuzik.slidingpuzzle2.di.modules.NetModule;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.helpers.PrefsHelper;
import zmuzik.slidingpuzzle2.view.PuzzleBoardView;

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

    void inject(PrefsHelper helper);

    Context getContext();

    Application getApplication();

    SharedPreferences getSharedPreferences();

    PrefsHelper getPrefsHelper();

    FlickrApi getFlickrApi();
}