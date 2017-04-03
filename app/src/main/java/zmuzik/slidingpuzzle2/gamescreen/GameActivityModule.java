package zmuzik.slidingpuzzle2.gamescreen;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zmuzik.slidingpuzzle2.common.di.ActivityContext;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@Module
public class GameActivityModule {

    private GameActivity mActivity;

    public GameActivityModule(GameActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    GameScreenPresenter providePresenter() {
        return new GameScreenPresenter();
    }

    @Provides
    @ActivityScope
    GameScreenView provideView() {
        return mActivity;
    }

}
