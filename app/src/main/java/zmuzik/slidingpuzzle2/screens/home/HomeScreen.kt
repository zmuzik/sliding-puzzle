package zmuzik.slidingpuzzle2.screens.home

import android.widget.ImageView
import zmuzik.slidingpuzzle2.common.PictureTab

interface HomeScreen {

    fun runGame(itemView: ImageView, tab: PictureTab, position: Int): Boolean

    fun requestReadExternalPermission()
}
