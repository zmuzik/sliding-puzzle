package zmuzik.slidingpuzzle2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin
import org.koin.log.Logger
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin(this, listOf(appModule), logger = object : Logger {
            override fun debug(msg: String) {}

            override fun err(msg: String) {}

            override fun info(msg: String) {}
        })

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
        }
    }
}
