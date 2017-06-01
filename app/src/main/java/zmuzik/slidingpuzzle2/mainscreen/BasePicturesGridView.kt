package zmuzik.slidingpuzzle2.mainscreen

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.pictures_grid.view.*
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.Utils
import javax.inject.Inject

/**
 * Created by Zbynek Muzik on 2017-03-28.
 */

open class BasePicturesGridView(context: Context) : RelativeLayout(context) {

    @Inject
    lateinit var presenter: MainScreenPresenter

    var mAdapter: PicturesGridAdapter? = null

    init {
        init()
    }

    open fun init() {
        LayoutInflater.from(context).inflate(R.layout.pictures_grid, this)
        ButterKnife.bind(this, this)
        if (context is MainActivity) {
            (context as MainActivity).component.inject(this)
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, getColumnsNumber())
        fab.setOnClickListener { onFabClicked(it) }
        requestPermissionsButton.setOnClickListener { onRequestPermissionsButtonClicked(it) }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        requestUpdate()
    }

    fun isHorizontal() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    fun getColumnsNumber(): Int {
        if (Utils.isTablet(context)) {
            return if (isHorizontal())
                Conf.GRID_COLUMNS_LANDSCAPE_TABLET
            else
                Conf.GRID_COLUMNS_PORTRAIT_TABLET
        } else {
            return if (isHorizontal())
                Conf.GRID_COLUMNS_LANDSCAPE_HANDHELD
            else
                Conf.GRID_COLUMNS_PORTRAIT_HANDHELD
        }
    }

    fun setFabIcon(drawable: Drawable) {
        fab.setImageDrawable(drawable)
        fab.visibility = View.VISIBLE
    }

    open fun onFabClicked(fab: View) {
    }

    open fun onRequestPermissionsButtonClicked(fab: View) {
    }

    open fun requestUpdate() {
        // this should call the presenter to request pictures
    }

    // this should be called by the presenter (via proxy/activity) to update the pictures
    open fun update(uris: List<String>) {
        progressBar.visibility = View.GONE
        mAdapter = PicturesGridAdapter(context, uris, getColumnsNumber())
        recyclerView.adapter = mAdapter
    }
}
