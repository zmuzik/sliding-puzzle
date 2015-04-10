package zmuzik.slidingpuzzle2;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import zmuzik.slidingpuzzle2.flickr.FlickrApi;
import zmuzik.slidingpuzzle2.flickr.Photo;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();

    private static App mApp;

    private List<Photo> mFlickrPhotos = new ArrayList<>();

    private FlickrApi mFlickrApi;

    public static App get() {
        return mApp;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "====================Initializing app====================");
        mApp = this;
        super.onCreate();
        Crashlytics.start(this);
        initFlickrApi();
    }

    void initFlickrApi() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(FlickrConf.FLICKR_API_ROOT);
        if (isDebuggable()) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new RestAdapter.Log() {
                public void log(String msg) {
                    Log.i("RETROFIT", msg);
                }
            });
        }
        RestAdapter restAdapter = builder.build();
        mFlickrApi = restAdapter.create(FlickrApi.class);
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
}
