package zmuzik.slidingpuzzle2;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import zmuzik.slidingpuzzle2.di.components.AppComponent;
import zmuzik.slidingpuzzle2.di.components.DaggerAppComponent;
import zmuzik.slidingpuzzle2.di.modules.AppModule;

public class App extends Application {

    private final String TAG = this.getClass().getSimpleName();

    private static App mApp;
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

    public static App get(Context context) {
        return ((App) context.getApplicationContext());
    }

    public AppComponent getComponent(Context context) {
        return get(context).mAppComponent;
    }
}
