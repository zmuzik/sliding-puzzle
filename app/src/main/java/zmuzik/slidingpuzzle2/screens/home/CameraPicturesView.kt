package zmuzik.slidingpuzzle2.screens.home

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.pictures_grid.view.*
import zmuzik.slidingpuzzle2.common.PictureTab


class CameraPicturesView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        BasePicturesView(context, attrs, defStyleAttr) {

    override val tab = PictureTab.CAMERA

    val permissionsComboView: View by lazy { permissionsCombo }

    init {
        requestPermissionsButton.setOnClickListener { button ->
            homeScreen.requestReadExternalPermission()
        }
    }
}
