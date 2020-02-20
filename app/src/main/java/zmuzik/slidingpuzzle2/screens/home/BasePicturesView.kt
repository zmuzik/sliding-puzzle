package zmuzik.slidingpuzzle2.screens.home

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_pictures_grid.view.*
import kotlinx.android.synthetic.main.pictures_grid.view.*
import timber.log.Timber
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.common.*
import zmuzik.slidingpuzzle2.repo.model.Picture

open class BasePicturesView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.pictures_grid, this, true)
        recyclerView.layoutManager = GridLayoutManager(context, columnsNumber)
    }

    lateinit var homeScreen: HomeScreen

    open val tab = PictureTab.APP

    var adapter: PicturesGridAdapter? = null

    open fun onDataUpdate(resource: Resource<List<Picture>>?) = when (resource) {
        is Resource.Loading -> progressBar.show()
        is Resource.Success -> {
            progressBar.hide()
            adapter = getAdapter(resource.data ?: emptyList())
            recyclerView.adapter = adapter
        }
        is Resource.Failure -> progressBar.hide()
        null -> {
        }
    }

    val isHorizontal get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isTablet get() = resources.getBoolean(R.bool.isTablet)

    val columnsNumber: Int
        get() = when {
            isTablet && isHorizontal -> Conf.GRID_COLS_TABLET_LANDSCAPE
            isTablet && !isHorizontal -> Conf.GRID_COLS_TABLET_PORTRAIT
            !isTablet && isHorizontal -> Conf.GRID_COLS_PHONE_LANDSCAPE
            else -> Conf.GRID_COLS_PHONE_PORTRAIT
        }

    open fun getAdapter(uris: List<Picture>): PicturesGridAdapter = PicturesGridAdapter(uris, columnsNumber)

    inner class PicturesGridAdapter(val pictures: List<Picture>, val columns: Int) :
            RecyclerView.Adapter<PicturesGridAdapter.ViewHolder>() {

        var dim: Int = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesGridAdapter.ViewHolder {
            dim = parent.width / columns
            return ViewHolder(parent.inflate(R.layout.item_pictures_grid))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        fun setOrientationIcon(orientationIcon: ImageView, position: Int) {
            val picture = pictures[position]
            if (picture.isHorizontal == null) {
                picture.isHorizontal = when (picture) {
                    is Picture.LocalPicture -> isBitmapHorizontal(orientationIcon.context, picture.thumbUrl)
                    is Picture.FlickrPicture -> picture.flickrPhoto.isHorizontal
                }
            }
            orientationIcon.visibility = View.VISIBLE
            picture.isHorizontal?.let { orientationIcon.rotation = if (it) 270f else 0f }
        }

        override fun getItemCount() = pictures.size

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            fun bind(position: Int) {
                val uriString = pictures[position].thumbUrl
                itemView.itemProgressBar.show()
                Picasso.with(view.context).cancelRequest(itemView.image)
                Picasso.with(view.context).load(uriString)
                        .resize(dim, dim)
                        .centerCrop()
                        .into(itemView.image, object : Callback {
                            override fun onSuccess() {
                                itemView.itemProgressBar.hide()
                                itemView.orientationIcon.show()
                                setOrientationIcon(itemView.orientationIcon, position)
                            }

                            override fun onError() {
                                itemView.itemProgressBar.hide()
                                itemView.orientationIcon.hide()
                                itemView.image.setImageResource(R.drawable.ic_panorama_32dp)
                                Timber.e("$uriString failed")
                            }
                        })
                itemView.image.setOnClickListener {
                    val willOpen = homeScreen.runGame(itemView.image, tab, position)
                    itemView.itemProgressBar.showIf(willOpen)
                }
            }
        }
    }
}
