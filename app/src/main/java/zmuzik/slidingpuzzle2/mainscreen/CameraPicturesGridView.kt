package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.pictures_grid.view.*
import zmuzik.slidingpuzzle2.R

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

class CameraPicturesGridView(context: Context) : BasePicturesGridView(context) {

    override fun init() {
        super.init()
        fab.setImageDrawable(resources.getDrawable(R.drawable.ic_photo_camera_24dp))
        fab.visibility = View.VISIBLE
        permissionsCombo.visibility = if (presenter.isReadExternalGranted) View.GONE else View.VISIBLE
    }

    override fun onRequestPermissionsButtonClicked(fab: View) {
        presenter.requestReadExternalPermission()
    }

    override fun onFabClicked(fab: View) {
        presenter.launchCameraApp()
    }

    override fun requestUpdate() {
        presenter.requestUpdateCameraPictures()
        progressBar.visibility = View.VISIBLE
    }

    fun setWaitingForPictures() {
        permissionsCombo.visibility = if (presenter.isReadExternalGranted) View.GONE else View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    override fun update(pictures: List<String>) {
        super.update(pictures)
        permissionsCombo.visibility = if (presenter.isReadExternalGranted) View.GONE else View.VISIBLE
    }
}
