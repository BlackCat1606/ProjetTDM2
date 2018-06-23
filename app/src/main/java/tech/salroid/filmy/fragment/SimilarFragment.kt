package tech.salroid.filmy.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.SimilarMovieActivityAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.data_classes.SimilarMoviesData
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork



class SimilarFragment : Fragment(), SimilarMovieActivityAdapter.ClickListener {

    @BindView(R.id.similar_recycler)
    internal var similar_recycler: RecyclerView? = null
    @BindView(R.id.breathingProgressFragment)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.card_holder)
    internal var card_holder: TextView? = null
    @BindView(R.id.detail_fragment_views_layout)
    internal var relativeLayout: RelativeLayout? = null
    private var similar_json: String? = null
    private var movieId: String? = null
    private var movieTitle: String? = null
    private val api_key = BuildConfig.TMDB_API_KEY

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.similar_fragment, container, false)
        ButterKnife.bind(this, view)

        val llm = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        similar_recycler!!.layoutManager = llm
        similar_recycler!!.isNestedScrollingEnabled = false

        similar_recycler!!.visibility = View.INVISIBLE

        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedBundle = arguments

        if (savedBundle != null) {

            movieId = savedBundle.getString("movie_id")
            movieTitle = savedBundle.getString("movie_title")

        }


        if (movieId != null)
            getSimilarFromNetwork(movieId!!)

    }


    fun getSimilarFromNetwork(movieId: String) {

        val BASE_MOVIE_CAST_DETAILS = " https://api.themoviedb.org/3/movie/$movieId/similar?api_key=$api_key"
        val jsonObjectRequestForMovieCastDetails = JsonObjectRequest(BASE_MOVIE_CAST_DETAILS, null,
                Response.Listener<JSONObject> { response ->
                    similar_json = response.toString()
                    similar_parseOutput(response.toString())
                }, Response.ErrorListener { error ->
            Log.e("webi", "Volley Error: " + error.cause)

            breathingProgress!!.visibility = View.GONE
        }
        )


        val volleySingleton = TmdbVolleySingleton.instance
        val requestQueue = volleySingleton!!.requestQueue
        requestQueue.add(jsonObjectRequestForMovieCastDetails)
    }


    private fun similar_parseOutput(similar_result: String) {

        val par = MovieDetailsActivityParseWork(activity, similar_result)

        val similar_list = par.parse_similar_movies()

        val similar_adapter = SimilarMovieActivityAdapter(activity, similar_list, true)
        similar_adapter.setClickListener(this)
        similar_recycler!!.adapter = similar_adapter

        if (similar_list.size == 0) {
            card_holder!!.visibility = View.INVISIBLE
        }

        breathingProgress!!.visibility = View.GONE
        similar_recycler!!.visibility = View.VISIBLE

        relativeLayout!!.minimumHeight = 0
    }

    override fun itemClicked(setterGetter: SimilarMoviesData, position: Int, view: View) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", setterGetter.movie_title)
        intent.putExtra("id", setterGetter.movie_id)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)

        startActivity(intent)

    }

    companion object {

        fun newInstance(movie_Id: String, movie_Title: String): SimilarFragment {
            val fragment = SimilarFragment()
            val args = Bundle()
            args.putString("movie_id", movie_Id)
            args.putString("movie_title", movie_Title)
            fragment.arguments = args
            return fragment
        }
    }
}
