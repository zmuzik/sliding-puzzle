package zmuzik.slidingpuzzle2.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast

import javax.inject.Inject

/**
 * Created by Zbynek Muzik on 2017-03-30.
 * Convenience class for showing show messages from any thread
 * using the app context to prevent crashes.
 */

class Toaster @Inject
constructor(internal var application: Application) {

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT

    fun show(stringId: Int) = show(application.getString(stringId))

    fun show(stringId: Int, length: Int) = show(application.getString(stringId), length)

    fun show(message: String) = show(message, Toast.LENGTH_SHORT)

    fun show(message: String, length: Int) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(application, message, length).show()
        } else {
            mainThreadHandler.post { Toast.makeText(application, message, length).show() }
        }
    }
}
