package tech.salroid.filmy.fragment

import android.animation.Animator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R




class FullReadFragment : Fragment(), View.OnClickListener {

    internal lateinit  var titleValue: String
    internal lateinit var descValue: String

    @BindView(R.id.textViewTitle) internal var title: TextView? = null
    @BindView(R.id.textViewDesc) internal var desc: TextView? = null
    @BindView(R.id.cross) internal var crossButton: ImageView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.read_full_layout, container, false)
        ButterKnife.bind(this, view)


        crossButton!!.setOnClickListener(this)


        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                        oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)

                val cx = arguments.getInt("cx")
                val cy = arguments.getInt("cy")

                // get the hypothenuse so the radius is from one corner to the other
                val radius = Math.hypot(right.toDouble(), bottom.toDouble()).toInt()

                val reveal: Animator

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, radius.toFloat())
                    reveal.interpolator = DecelerateInterpolator(2f)
                    reveal.duration = 1000
                    reveal.start()
                }

            }
        })

        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleValue = arguments.getString("title", " ")
        descValue = arguments.getString("desc", " ")

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title!!.text = titleValue
        desc!!.text = descValue
    }

    override fun onClick(view: View) {
        fragmentManager.popBackStack()

    }

}
