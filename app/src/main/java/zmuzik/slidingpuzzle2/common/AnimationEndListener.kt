package zmuzik.slidingpuzzle2.common

import android.view.animation.Animation

class AnimationEndListener(val onEnd: (animation: Animation?) -> Unit) : Animation.AnimationListener {

    override fun onAnimationEnd(animation: Animation?) = onEnd(animation)

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationStart(animation: Animation?) {}
}