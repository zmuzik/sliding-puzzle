package zmuzik.slidingpuzzle2.screens

import android.util.DisplayMetrics
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    val mainActivity get() = activity as? MainActivity

    val screenWidth: Int by lazy {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    val screenHeight: Int by lazy {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}