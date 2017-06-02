package zmuzik.slidingpuzzle2

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.webkit.MimeTypeMap

import java.io.File
import java.io.IOException
import java.io.InputStream


object Utils {

    val ASSET_PREFIX = "file:///android_asset/"
    val FILE_PREFIX = "file://"

    fun isBitmapHorizontal(application: Application, filePath: String): Boolean {
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        if (isAsset(filePath)) {
            try {
                // use application context because the activity may be theoretically GCed
                val stream = application.assets.open(getAssetName(filePath))
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

    fun isAsset(filePath: String): Boolean {
        return filePath.startsWith(ASSET_PREFIX)
    }

    fun getAssetName(filePath: String): String {
        return filePath.substring(ASSET_PREFIX.length)
    }

    fun isFile(filePath: String): Boolean {
        return filePath.startsWith(FILE_PREFIX) && !filePath.startsWith(ASSET_PREFIX)
    }

    fun getFileName(filePath: String): String {
        return filePath.substring(FILE_PREFIX.length)
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

    fun isDebuggable(context: Context): Boolean {
        return 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.isTablet)
    }
}

