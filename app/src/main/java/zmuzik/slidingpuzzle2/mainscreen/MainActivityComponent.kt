package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import dagger.Component
import zmuzik.slidingpuzzle2.common.di.ActivityScope
import zmuzik.slidingpuzzle2.common.di.AppComponent

/**
 * Created by Zbynek Muzik on 2017-03-30.
 */

@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(presenter: MainScreenPresenter)

    fun inject(adapter: PicturesGridAdapter)

    fun inject(gridView: BasePicturesGridView)

    fun inject(gridView: SavedPicturesGridView)

    val activityContext: Context
}
