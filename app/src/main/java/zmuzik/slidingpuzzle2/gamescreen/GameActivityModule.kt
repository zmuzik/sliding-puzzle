package zmuzik.slidingpuzzle2.gamescreen

import android.app.Activity
import android.content.Context

import dagger.Module
import dagger.Provides
import zmuzik.slidingpuzzle2.common.ShakeDetector
import zmuzik.slidingpuzzle2.common.di.ActivityContext
import zmuzik.slidingpuzzle2.common.di.ActivityScope

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@Module
class GameActivityModule(private val mActivity: GameActivity) {

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
    internal fun providePresenter(): GameScreenPresenter {
        return GameScreenPresenter()
    }

    @Provides
    @ActivityScope
    internal fun provideView(): GameScreenView {
        return mActivity
    }

    @Provides
    @ActivityScope
    internal fun provideShakeDetector(): ShakeDetector {
        return ShakeDetector(mActivity)
    }

}
