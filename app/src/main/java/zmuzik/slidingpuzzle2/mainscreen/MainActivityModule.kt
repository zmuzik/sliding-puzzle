package zmuzik.slidingpuzzle2.mainscreen

import android.app.Activity
import android.content.Context

import dagger.Module
import dagger.Provides
import zmuzik.slidingpuzzle2.common.di.ActivityContext
import zmuzik.slidingpuzzle2.common.di.ActivityScope

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@Module
class MainActivityModule(private val mActivity: Activity) {

    @Provides
    @ActivityScope
    internal fun provideContext(): Context {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun provideActivity(): Activity {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun providePresenter(): MainScreenPresenter {
        return MainScreenPresenter()
    }

    @Provides
    @ActivityScope
    internal fun provideView(): MainScreenView {
        return mActivity as MainActivity
    }

}
