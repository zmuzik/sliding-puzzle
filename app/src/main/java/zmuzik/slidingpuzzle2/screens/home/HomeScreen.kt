package zmuzik.slidingpuzzle2.screens.home

import android.view.View
import zmuzik.slidingpuzzle2.common.PictureTab

interface HomeScreen {

    fun runGame(itemView: View, tab: PictureTab, position: Int): Boolean

    fun requestReadExternalPermission()
}
