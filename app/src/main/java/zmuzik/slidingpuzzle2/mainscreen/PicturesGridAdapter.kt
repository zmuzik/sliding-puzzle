package zmuzik.slidingpuzzle2.mainscreen

import android.app.Application
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_pictures_grid.view.*
import zmuzik.slidingpuzzle2.Conf
import zmuzik.slidingpuzzle2.R
import zmuzik.slidingpuzzle2.isBitmapHorizontal
import java.util.*
import javax.inject.Inject

open class PicturesGridAdapter(val context: Context, val uris: List<String>?, val columns: Int) :
        RecyclerView.Adapter<PicturesGridAdapter.ViewHolder>() {

    val TAG = this.javaClass.simpleName

    var pictures: MutableList<OrientedPicture>? = null
    var dim: Int = 0
    var page = 1

    @Inject
    lateinit var presenter: MainScreenPresenter
    @Inject
    lateinit var application: Application

    init {
        pictures = ArrayList<OrientedPicture>()
        if (uris != null) {
            for (uri in uris) {
                pictures!!.add(OrientedPicture(uri))
            }
        }
        if (context is MainActivity) {
            context.component.inject(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesGridAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pictures_grid, parent, false)
        dim = parent.width / columns
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isFooterItem(position)) {
            holder.bindFooterItem()
        } else {
            holder.bindRegularItem(position)
        }
    }

    open fun runGame(position: Int) {
        val picture = pictures!![position]
        presenter!!.runGame(picture.uri, picture.isHorizontal())
    }

    private fun showNextPage() {
        var startPosition = itemCount - 1
        page++
        notifyItemChanged(startPosition++)
        val endPosition = itemCount
        for (i in startPosition..endPosition - 1) {
            notifyItemInserted(i)
        }
    }

    open fun setOrientationIcon(orientationIcon: ImageView, position: Int) {
        orientationIcon.visibility = View.VISIBLE
        val picture = pictures!![position]

        orientationIcon.rotation = if (picture.isHorizontal()) 270f else 0f
    }

    internal open val pageSize: Int
        get() = Conf.PAGE_SIZE

    private val displayedPicsCount: Int
        get() {
            if (pictures == null) return 0
            return if (pictures!!.size < pageSize * page) pictures!!.size else pageSize * page
        }

    private val isMoreToDisplay: Boolean
        get() {
            if (pictures == null) return false
            return pictures!!.size > pageSize * page
        }

    override fun getItemCount(): Int {
        if (pictures == null) return 0
        return if (isMoreToDisplay) displayedPicsCount + 1 else displayedPicsCount
    }

    private fun isFooterItem(position: Int): Boolean {
        return position == itemCount - 1 && isMoreToDisplay
    }

    inner class OrientedPicture(var uri: String) {
        var isHorizontal: Boolean? = null

        fun setIsHorizontal(isHorizontal: Boolean) {
            this.isHorizontal = isHorizontal
        }

        fun isHorizontal(): Boolean {
            if (isHorizontal == null) {
                isHorizontal = isBitmapHorizontal(application, uri)
            }
            return isHorizontal!!
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindRegularItem(position: Int) {
            val uriString = pictures!![position].uri
            itemView.nextTv.visibility = View.GONE
            itemView.progressBar.visibility = View.VISIBLE
            Picasso.with(context).cancelRequest(itemView.image)
            Picasso.with(context).load(uriString)
                    .resize(dim, dim)
                    .centerCrop()
                    .into(itemView.image, object : Callback {
                        override fun onSuccess() {
                            itemView.image.visibility = View.VISIBLE
                            itemView.progressBar.visibility = View.GONE
                            itemView.orientationIcon.visibility = View.VISIBLE
                            setOrientationIcon(itemView.orientationIcon, position)
                        }

                        override fun onError() {
                            itemView.image.visibility = View.VISIBLE
                            itemView.progressBar.visibility = View.GONE
                            itemView.orientationIcon.visibility = View.GONE
                            itemView.image.setImageResource(R.drawable.ic_panorama_32dp)
                            Log.e("PicturesGridAdapter", uriString + " failed")
                        }
                    })
            itemView.image.setOnClickListener { runGame(position) }
        }

        fun bindFooterItem() {
            itemView.image.visibility = View.GONE
            itemView.progressBar.visibility = View.GONE
            itemView.orientationIcon.visibility = View.GONE
            itemView.nextTv.visibility = View.VISIBLE
            itemView.nextTv.setOnClickListener { showNextPage() }
        }
    }
}
