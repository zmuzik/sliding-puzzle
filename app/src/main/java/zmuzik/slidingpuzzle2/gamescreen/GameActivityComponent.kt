package zmuzik.slidingpuzzle2.gamescreen

import dagger.Component
import zmuzik.slidingpuzzle2.common.di.ActivityScope
import zmuzik.slidingpuzzle2.common.di.AppComponent

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GameActivityModule::class))
interface GameActivityComponent {

    fun inject(mainActivity: GameActivity)

    fun inject(presenter: GameScreenPresenter)

    fun inject(view: PuzzleBoardView)

}
