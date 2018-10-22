package zmuzik.slidingpuzzle2.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

fun View.hide(): Unit = this.setVisibility(View.GONE)

fun View.show(): Unit = this.setVisibility(View.VISIBLE)

fun View.showIf(boolean: Boolean): Unit = this.setVisibility(if (boolean) View.VISIBLE else View.GONE)

fun View.getRelativeLeft(): Int = if (this.parent === this.rootView)
    this.left
else
    this.left + (this.parent as View).getRelativeLeft()

fun View.getRelativeTop(): Int = if (this.parent === this.rootView)
    this.top
else
    this.top + (this.parent as View).getRelativeTop()

fun ViewGroup.inflate(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

fun Context.toast(msg: String) {
    fun showToast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    if (Looper.myLooper() == Looper.getMainLooper()) {
        showToast(msg)
    } else {
        Handler(Looper.getMainLooper()).post { showToast(msg) }
    }
}

fun Context.toast(stringId: Int) = toast(applicationContext.getString(stringId))
