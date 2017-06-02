package zmuzik.slidingpuzzle2

import android.app.Application
import android.content.Context

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric
import zmuzik.slidingpuzzle2.common.di.AppComponent
import zmuzik.slidingpuzzle2.common.di.AppModule
import zmuzik.slidingpuzzle2.common.di.DaggerAppComponent

class App : Application() {

    private val TAG = this.javaClass.simpleName

    lateinit var mAppComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        mAppComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        Fabric.with(this, Crashlytics())
    }

    fun getComponent(context: Context) = (context.applicationContext as App).mAppComponent
}
