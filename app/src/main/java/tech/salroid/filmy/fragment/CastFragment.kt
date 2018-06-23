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

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.CharacterDetailsActivity
import tech.salroid.filmy.activities.FullCastActivity
import tech.salroid.filmy.custom_adapter.CastAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.data_classes.CastDetailsData
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork



class CastFragment : Fragment(), View.OnClickListener, CastAdapter.ClickListener {


    @BindView(R.id.more)
    internal var more: TextView? = null
    @BindView(R.id.cast_recycler)
    internal var cast_recycler: RecyclerView? = null
    @BindView(R.id.card_holder)
    internal var card_holder: TextView? = null
    @BindView(R.id.breathingProgressFragment)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.detail_fragment_views_layout)
    internal var relativeLayout: RelativeLayout? = null
    private var cast_json: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null
    private var gotCrewListener: GotCrewListener? = null
    private val api_key = BuildConfig.TMDB_API_KEY

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.cast_fragment, container, false)
        ButterKnife.bind(this, view)

        cast_recycler!!.layoutManager = LinearLayoutManager(activity)
        cast_recycler!!.isNestedScrollingEnabled = false

        cast_recycler!!.visibility = View.INVISIBLE

        more!!.setOnClickListener(this)


        return view
    }


    fun setGotCrewListener(gotCrewListener: GotCrewListener) {

        this.gotCrewListener = gotCrewListener

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedBundle = arguments

        if (savedBundle != null) {

            movieId = savedBundle.getString("movie_id")
            movieTitle = savedBundle.getString("movie_title")

        }


        if (movieId != null)
            getCastFromNetwork(movieId!!)

    }


    fun getCastFromNetwork(movieId: String) {


        val BASE_MOVIE_CAST_DETAILS = "http://api.themoviedb.org/3/movie/$movieId/casts?api_key=$api_key"
        val jsonObjectRequestForMovieCastDetails = JsonObjectRequest(BASE_MOVIE_CAST_DETAILS, null,
                Response.Listener<JSONObject> { response ->
                    cast_json = response.toString()

                    cast_parseOutput(response.toString())

                    if (gotCrewListener != null)
                        gotCrewListener!!.gotCrew(response.toString())
                }, Response.ErrorListener { error ->
            Log.e("webi", "Volley Error: " + error.cause)

            breathingProgress!!.visibility = View.GONE
        }
        )


        val volleySingleton = TmdbVolleySingleton.instance
        val requestQueue = volleySingleton!!.requestQueue
        requestQueue.add(jsonObjectRequestForMovieCastDetails)
    }


    private fun cast_parseOutput(cast_result: String) {

        val par = MovieDetailsActivityParseWork(activity, cast_result)
        val cast_list = par.parse_cast()
        val cast_adapter = CastAdapter(activity, cast_list, true)
        cast_adapter.setClickListener(this)
        cast_recycler!!.adapter = cast_adapter
        if (cast_list.size > 4)
            more!!.visibility = View.VISIBLE
        else if (cast_list.size == 0) {
            more!!.visibility = View.INVISIBLE
            card_holder!!.visibility = View.INVISIBLE
        } else
            more!!.visibility = View.INVISIBLE

        breathingProgress!!.visibility = View.GONE
        cast_recycler!!.visibility = View.VISIBLE

        relativeLayout!!.minimumHeight = 0
    }

    override fun onClick(view: View) {

        if (view.id == R.id.more) {

            Log.d("webi", "" + movieTitle!!)

            if (cast_json != null && movieTitle != null) {


                val intent = Intent(activity, FullCastActivity::class.java)
                intent.putExtra("cast_json", cast_json)
                intent.putExtra("toolbar_title", movieTitle)
                startActivity(intent)

            }
        }


    }

    override fun itemClicked(setterGetter: CastDetailsData, position: Int, view: View) {
        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.cast_id)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            val p1 = Pair.create(view.findViewById<View>(R.id.cast_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.cast_name), "name")

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1, p2)
            startActivity(intent, options.toBundle())

        } else {
            startActivity(intent)
        }

    }


    interface GotCrewListener {
        fun gotCrew(crewData: String)
    }

    companion object {


        fun newInstance(movie_Id: String, movie_Title: String): CastFragment {
            val fragment = CastFragment()
            val args = Bundle()
            args.putString("movie_id", movie_Id)
            args.putString("movie_title", movie_Title)
            fragment.arguments = args
            return fragment
        }
    }

}