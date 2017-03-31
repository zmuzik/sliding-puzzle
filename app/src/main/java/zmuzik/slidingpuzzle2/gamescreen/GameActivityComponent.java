package zmuzik.slidingpuzzle2.gamescreen;

import dagger.Component;
import zmuzik.slidingpuzzle2.common.di.AppComponent;
import zmuzik.slidingpuzzle2.common.di.ActivityScope;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(
        dependencies = AppComponent.class,
        modules = GameScreenModule.class
)
public interface GameActivityComponent {

    void inject(GameActivity mainActivity);

}
