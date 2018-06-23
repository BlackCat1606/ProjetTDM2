package tech.salroid.filmy.animations

/**
 * Created by BlackCat on 6/21/2018.
 */
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.FrameLayout

object RevealAnimation {

    fun performReveal(allDetails: FrameLayout?) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (allDetails != null) {

                val viewTreeObserver = allDetails.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            circularRevealActivity(allDetails)
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                allDetails.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                allDetails.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }
                    })
                }

            }
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun circularRevealActivity(allDetails: FrameLayout?) {

        val cx = allDetails!!.width / 2
        val cy = allDetails.height / 2

        val finalRadius = Math.max(allDetails.width, allDetails.height).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal = ViewAnimationUtils.createCircularReveal(allDetails, cx, cy, 0f, finalRadius)
        circularReveal.duration = 1000

        // make the view visible and start the animation
        allDetails.visibility = View.VISIBLE
        circularReveal.start()
    }


}

