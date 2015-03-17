package zmuzik.slidingpuzzle;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.otto.Bus;

import retrofit.RestAdapter;
import zmuzik.slidingpuzzle.flickr.FlickrApi;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();
    private static final Bus BUS = new Bus();
    private static App mApp;

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
        initFlickrApi();
    }

    void initFlickrApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Conf.FLICKR_API_ROOT)
                .build();
        mFlickrApi = restAdapter.create(FlickrApi.class);
    }

    public FlickrApi getFlickrApi() {
        return mFlickrApi;
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
}
