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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zmuzik.slidingpuzzle2.di.components.AppComponent;
import zmuzik.slidingpuzzle2.di.components.DaggerAppComponent;
import zmuzik.slidingpuzzle2.di.modules.AppModule;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;

public class App extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private static App mApp;
    private List<Photo> mFlickrPhotos = new ArrayList<>();
    private FlickrApi mFlickrApi;

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        mApp = this;
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        Fabric.with(this, new Crashlytics());
        initFlickrApi();
    }

    void initFlickrApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Conf.FLICKR_API_ROOT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mFlickrApi = retrofit.create(FlickrApi.class);
    }

    public static App get() {
        return mApp;
    }

    public FlickrApi getFlickrApi() {
        return mFlickrApi;
    }

    public List<Photo> getFlickrPhotos() {
        return mFlickrPhotos;
    }

    public void setFlickrPhotos(List<Photo> photos) {
        mFlickrPhotos = photos;
    }

    public boolean isDebuggable() {
        return 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public static AppComponent getComponent(Context context) {
        return ((App) context.getApplicationContext()).mAppComponent;
    }
}
