package zmuzik.slidingpuzzle2.common

data class FileContainer(val filePath: String, val lastModified: Long)

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Failure<out T>(val code: Int) : Resource<T>()
}

enum class PictureTab {
    APP,
    CAMERA,
    FLICKR
}