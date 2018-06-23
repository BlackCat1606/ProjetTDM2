package tech.salroid.filmy.fragment


import android.animation.Animator
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.google.android.youtube.player.YouTubeStandalonePlayer

import java.util.Arrays

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.TrailerAdapter

class AllTrailerFragment : Fragment(), View.OnClickListener, TrailerAdapter.OnItemClickListener {

    internal lateinit var titleValue: String
    internal var trailers: Array<String>? = null
    internal var trailers_name: Array<String>? = null
    internal lateinit var recyclerView: RecyclerView

    @BindView(R.id.textViewTitle)
    internal var title: TextView? = null
    @BindView(R.id.cross)
    internal var crossButton: ImageView? = null
    @BindView(R.id.main_content)
    internal var mainContent: RelativeLayout? = null

    private var nightMode: Boolean = false


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        nightMode = sp.getBoolean("dark", false)


        val view = inflater!!.inflate(R.layout.all_trailer_layout, container, false)
        ButterKnife.bind(this, view)

        if (!nightMode)
            allThemeLogic()
        else {
            nightModeLogic()
        }

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

        init(view)

        return view
    }

    private fun nightModeLogic() {
        crossButton!!.setImageDrawable(resources.getDrawable(R.drawable.ic_action_navigation_close_inverted))
        mainContent!!.setBackgroundColor(Color.parseColor("#212121"))
        title!!.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun allThemeLogic() {
        crossButton!!.setImageDrawable(resources.getDrawable(R.drawable.ic_close_black_48dp))
        mainContent!!.setBackgroundColor(Color.parseColor("#ffffff"))
        title!!.setTextColor(Color.parseColor("#000000"))

    }

    private fun init(view: View) {
        recyclerView = view.findViewById<View>(R.id.all_trailer_recycler_view) as RecyclerView
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleValue = arguments.getString("title", " ")
        trailers = arguments.getStringArray("trailers")
        trailers_name = arguments.getStringArray("trailers_name")

    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        title!!.text = titleValue
        val trailerAdapter = TrailerAdapter(trailers!!, trailers_name!!, activity)
        trailerAdapter.setOnItemClickListener(this)
        recyclerView.adapter = trailerAdapter

    }

    override fun onClick(view: View) {
        fragmentManager.popBackStack()
    }

    override fun itemClicked(trailerId: String) {
        startActivity(YouTubeStandalonePlayer.createVideoIntent(activity,
                getString(R.string.Youtube_Api_Key), trailerId))
    }
}
