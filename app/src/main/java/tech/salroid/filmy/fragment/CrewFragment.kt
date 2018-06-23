package tech.salroid.filmy.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.CharacterDetailsActivity
import tech.salroid.filmy.activities.FullCrewActivity
import tech.salroid.filmy.custom_adapter.CrewAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.data_classes.CrewDetailsData
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork



class CrewFragment : Fragment(), View.OnClickListener, CrewAdapter.ClickListener {


    @BindView(R.id.crew_more)
    internal var more: TextView? = null
    @BindView(R.id.crew_recycler)
    internal var crew_recycler: RecyclerView? = null
    @BindView(R.id.card_holder)
    internal var card_holder: TextView? = null
    @BindView(R.id.breathingProgressFragment)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.detail_fragment_views_layout)
    internal var relativeLayout: RelativeLayout? = null
    private var crew_json: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.crew_fragment, container, false)
        ButterKnife.bind(this, view)

        crew_recycler!!.layoutManager = LinearLayoutManager(activity)
        crew_recycler!!.isNestedScrollingEnabled = false

        crew_recycler!!.visibility = View.INVISIBLE

        more!!.setOnClickListener(this)


        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedBundle = arguments

        if (savedBundle != null) {

            movieId = savedBundle.getString("movie_id")
            movieTitle = savedBundle.getString("movie_title")

        }

    }


    fun crew_parseOutput(crew_result: String) {

        val par = MovieDetailsActivityParseWork(activity, crew_result)
        val crew_list = par.parse_crew()

        crew_json = crew_result

        val crew_adapter = CrewAdapter(activity, crew_list, true)
        crew_adapter.setClickListener(this)
        crew_recycler!!.adapter = crew_adapter


        if (crew_list.size > 4)
            more!!.visibility = View.VISIBLE
        else if (crew_list.size == 0) {
            more!!.visibility = View.INVISIBLE
            card_holder!!.visibility = View.INVISIBLE
        } else
            more!!.visibility = View.INVISIBLE

        breathingProgress!!.visibility = View.GONE
        crew_recycler!!.visibility = View.VISIBLE

        relativeLayout!!.minimumHeight = 0
    }


    override fun onClick(view: View) {

        if (view.id == R.id.crew_more) {

            Log.d("webi", "" + movieTitle!!)

            if (crew_json != null && movieTitle != null) {

                val intent = Intent(activity, FullCrewActivity::class.java)
                intent.putExtra("crew_json", crew_json)
                intent.putExtra("toolbar_title", movieTitle)
                startActivity(intent)

            }
        }


    }


    override fun itemClicked(setterGetter: CrewDetailsData, position: Int, view: View) {

        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.crew_id)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            val p1 = Pair.create(view.findViewById<View>(R.id.crew_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.crew_name), "name")

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1, p2)
            startActivity(intent, options.toBundle())

        } else {
            startActivity(intent)
        }

    }

    companion object {

        fun newInstance(movie_Id: String, movie_Title: String): CrewFragment {

            val fragment = CrewFragment()
            val args = Bundle()
            args.putString("movie_id", movie_Id)
            args.putString("movie_title", movie_Title)
            fragment.arguments = args
            return fragment
        }
    }
}
