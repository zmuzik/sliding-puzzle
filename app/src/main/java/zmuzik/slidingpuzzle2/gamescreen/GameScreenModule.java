package zmuzik.slidingpuzzle2.gamescreen;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zmuzik.slidingpuzzle2.common.di.ActivityContext;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@Module
public class GameScreenModule {

    private Activity mActivity;

    public GameScreenModule(Activity activity) {
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

}
