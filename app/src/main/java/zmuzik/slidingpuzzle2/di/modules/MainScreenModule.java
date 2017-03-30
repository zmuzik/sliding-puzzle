package zmuzik.slidingpuzzle2.di.modules;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zmuzik.slidingpuzzle2.di.ActivityScope;
import zmuzik.slidingpuzzle2.mainscreen.MainActivity;
import zmuzik.slidingpuzzle2.mainscreen.MainScreenPresenter;
import zmuzik.slidingpuzzle2.mainscreen.MainScreenView;

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
    Context provideContext() {
        return mActivity;
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
