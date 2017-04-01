package zmuzik.slidingpuzzle2.mainscreen;

import dagger.Component;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;
import zmuzik.slidingpuzzle2.common.di.AppComponent;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = MainActivityModule.class
)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(MainScreenPresenter presenter);

    void inject(PicturesGridAdapter adapter);

    void inject(BasePicturesGridView gridView);

    void inject(SavedPicturesGridView gridView);

}
