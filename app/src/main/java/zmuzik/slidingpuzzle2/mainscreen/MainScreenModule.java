package zmuzik.slidingpuzzle2.mainscreen;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@Module
public class MainScreenModule {

    private final Activity mActivity;

    public MainScreenModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    MainScreenPresenter providePresenter() {
        return new MainScreenPresenter();
    }

    @Provides
    @ActivityScope
    MainScreenView provideView() {
        return (MainActivity) mActivity;
    }

}
