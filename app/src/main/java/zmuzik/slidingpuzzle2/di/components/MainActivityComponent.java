package zmuzik.slidingpuzzle2.di.components;

import dagger.Component;
import zmuzik.slidingpuzzle2.di.ActivityScope;
import zmuzik.slidingpuzzle2.di.modules.MainScreenModule;
import zmuzik.slidingpuzzle2.mainscreen.MainActivity;
import zmuzik.slidingpuzzle2.mainscreen.MainScreenPresenter;
import zmuzik.slidingpuzzle2.ui.fragments.CameraPicturesFragment;
import zmuzik.slidingpuzzle2.view.BasePicturesGridView;
import zmuzik.slidingpuzzle2.view.SavedPicturesGridView;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = MainScreenModule.class
)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(MainScreenPresenter presenter);

    void inject(BasePicturesGridView gridView);

    void inject(SavedPicturesGridView gridView);

    void inject(CameraPicturesFragment gridView);

}
