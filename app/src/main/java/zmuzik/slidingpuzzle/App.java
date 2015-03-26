package zmuzik.slidingpuzzle;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;

import java.util.List;

import retrofit.RestAdapter;
import zmuzik.slidingpuzzle.flickr.FlickrApi;
import zmuzik.slidingpuzzle.flickr.Photo;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();
    private static final Bus BUS = new Bus();
    private static App mApp;
    private List<Photo> mFlickrPhotos;

    private SQLiteDatabase mDb;
    private FlickrApi mFlickrApi;

    public static App get() {
        return mApp;
    }

    public static Bus getBus() {
        return BUS;
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
        builder.setEndpoint(Conf.FLICKR_API_ROOT);
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

    public int getAppVersionCode() {
        try {
            return App.get().getPackageManager().getPackageInfo(App.get().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }
}
