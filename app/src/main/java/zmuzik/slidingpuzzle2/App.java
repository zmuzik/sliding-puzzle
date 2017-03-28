package zmuzik.slidingpuzzle2;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import zmuzik.slidingpuzzle2.di.components.AppComponent;
import zmuzik.slidingpuzzle2.di.components.DaggerAppComponent;
import zmuzik.slidingpuzzle2.di.modules.AppModule;
import zmuzik.slidingpuzzle2.flickr.Photo;

public class App extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private static App mApp;
    private List<Photo> mFlickrPhotos = new ArrayList<>();
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        mApp = this;
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        Fabric.with(this, new Crashlytics());
    }

    public static App get() {
        return mApp;
    }

    public List<Photo> getFlickrPhotos() {
        return mFlickrPhotos;
    }

    public void setFlickrPhotos(List<Photo> photos) {
        mFlickrPhotos = photos;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static AppComponent getComponent(Context context) {
        return ((App) context.getApplicationContext()).mAppComponent;
    }
}
