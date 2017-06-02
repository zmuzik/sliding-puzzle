package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import kotlinx.android.synthetic.main.pictures_grid.view.*
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.flickr.Photo

/**
 * Created by Zbynek Muzik on 2017-03-31.
 */

class FlickrPicturesGridView(context: Context) : BasePicturesGridView(context) {

    override fun init() {
        super.init()
        fab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_24dp))
        fab.visibility = View.VISIBLE
    }

    override fun onFabClicked(fab: View) {
        val layout = LayoutInflater.from(context).inflate(R.layout.flickr_search_dialog, null)
        val keywordEt = layout.findViewById(R.id.keywordEt) as EditText
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.flickr_search)
        builder.setView(layout)
        builder.setPositiveButton(R.string.search) { dialog, which ->
            keywordEt.text?.let { search(it.toString()) }
            dialog?.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog?.dismiss()
        }
        val dialog = builder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()

        keywordEt.setOnEditorActionListener({ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keywordEt.text?.let { search(it.toString()) }
                dialog.dismiss()
                true
            } else {
                false
            }
        })

        keywordEt.setOnKeyListener({ v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                keywordEt.text?.let { search(it.toString()) }
                dialog.dismiss()
                true
            } else {
                false
            }
        })
    }

    private fun search(keywords: String) {
        presenter.requestFlickrSearch(keywords)
    }

    override fun requestUpdate() {
        presenter.requestUpdateFlickrPictures()
    }

    fun setWaitingForPictures() {
        progressBar.visibility = View.VISIBLE
    }

    fun updatePhotos(photos: List<Photo>) {
        progressBar.visibility = View.GONE
        mAdapter = FlickrGridAdapter(context, photos, getColumnsNumber())
        recyclerView.adapter = mAdapter
    }
}
