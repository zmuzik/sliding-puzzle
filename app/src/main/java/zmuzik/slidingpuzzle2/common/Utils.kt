package zmuzik.slidingpuzzle2.common

import android.content.Context
import android.graphics.BitmapFactory
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException

val ASSET_PREFIX = "file:///android_asset/"
val FILE_PREFIX = "file://"

fun isAsset(filePath: String) = filePath.startsWith(ASSET_PREFIX)

fun getAssetName(filePath: String) = filePath.substring(ASSET_PREFIX.length)

fun isFile(filePath: String) = filePath.startsWith(FILE_PREFIX) && !filePath.startsWith(ASSET_PREFIX)

fun getFileName(filePath: String) = filePath.substring(FILE_PREFIX.length)

fun isBitmapHorizontal(context: Context?, filePath: String): Boolean {
    val bmOptions = BitmapFactory.Options()
    bmOptions.inJustDecodeBounds = true
    if (isAsset(filePath)) {
        try {
            // use application context because the activity may be theoretically GCed
            val stream = context?.assets?.open(getAssetName(filePath)) ?: return false
            BitmapFactory.decodeStream(stream, null, bmOptions)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    } else if (isFile(filePath)) {
        BitmapFactory.decodeFile(getFileName(filePath), bmOptions)
    }

    return bmOptions.outWidth > bmOptions.outHeight
}

fun isPicture(file: File?): Boolean {
    if (file == null) return false
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
    if (extension != null) {
        val mime = MimeTypeMap.getSingleton()
        type = mime.getMimeTypeFromExtension(extension)
    }
    return type != null && type.startsWith("image")
}

fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}
