package zmuzik.slidingpuzzle2.flickr

class Size {
    var width: Int = 0
    var height: Int = 0
    var source: String? = null
    val maxDim: Int
        get() = if (width > height) width else height
}
