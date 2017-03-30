package zmuzik.slidingpuzzle2.di.components;

import dagger.Component;
import zmuzik.slidingpuzzle2.di.ActivityScope;
import zmuzik.slidingpuzzle2.di.modules.GameScreenModule;
import zmuzik.slidingpuzzle2.ui.activities.GameActivity;

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = GameScreenModule.class)
public interface GameActivityComponent {

    void inject(GameActivity mainActivity);

}
