package zmuzik.slidingpuzzle2.common

import androidx.transition.Transition

class TransitionEndListener(val onEnd: (transition: Transition) -> Unit) : Transition.TransitionListener {

    override fun onTransitionEnd(transition: Transition) = onEnd(transition)

    override fun onTransitionResume(transition: Transition) {}

    override fun onTransitionPause(transition: Transition) {}

    override fun onTransitionCancel(transition: Transition) {}

    override fun onTransitionStart(transition: Transition) {}
}