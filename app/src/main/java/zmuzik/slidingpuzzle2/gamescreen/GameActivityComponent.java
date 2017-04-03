package zmuzik.slidingpuzzle2.gamescreen;

import dagger.Component;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;
import zmuzik.slidingpuzzle2.common.di.AppComponent;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = GameActivityModule.class
)
public interface GameActivityComponent {

    void inject(GameActivity mainActivity);

    void inject(GameScreenPresenter presenter);

    void inject(PuzzleBoardView view);

}
