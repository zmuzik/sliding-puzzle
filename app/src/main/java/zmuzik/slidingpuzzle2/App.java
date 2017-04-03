package zmuzik.slidingpuzzle2;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import zmuzik.slidingpuzzle2.common.di.AppComponent;
import zmuzik.slidingpuzzle2.common.di.AppModule;
import zmuzik.slidingpuzzle2.common.di.DaggerAppComponent;

public class App extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        Fabric.with(this, new Crashlytics());
    }

    public static App get(Context context) {
        return ((App) context.getApplicationContext());
    }

    public AppComponent getComponent(Context context) {
        return get(context).mAppComponent;
    }
}
