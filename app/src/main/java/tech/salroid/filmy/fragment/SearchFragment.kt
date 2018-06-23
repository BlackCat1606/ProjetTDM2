package tech.salroid.filmy.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout

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
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.SearchResultAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.data_classes.SearchData
import tech.salroid.filmy.network_stuff.VolleySingleton
import tech.salroid.filmy.parser.SearchResultParseWork

import android.content.Context.INPUT_METHOD_SERVICE


class SearchFragment : Fragment(), SearchResultAdapter.ClickListener {


    internal lateinit var sadapter: SearchResultAdapter

    @BindView(R.id.search_results_recycler)
    internal var recycler: RecyclerView? = null
    @BindView(R.id.breathingProgress)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.fragment_rl)
    internal var fragmentRelativeLayout: RelativeLayout? = null
    private val api_key = BuildConfig.TMDB_API_KEY

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_search, container, false)
        ButterKnife.bind(this, view)

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val nightMode = sp.getBoolean("dark", false)


        if (nightMode)
            fragmentRelativeLayout!!.setBackgroundColor(activity.resources.getColor(R.color.black))
        else
            fragmentRelativeLayout!!.setBackgroundColor(activity.resources.getColor(R.color.grey))

        val tabletSize = resources.getBoolean(R.bool.isTablet)

        if (tabletSize) {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                val gridLayoutManager = StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager
            } else {
                val gridLayoutManager = StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager
            }

        } else {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                val gridLayoutManager = StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager
            } else {
                val gridLayoutManager = StaggeredGridLayoutManager(5,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager
            }

        }

        return view
    }


    override fun itemClicked(setterGetter: SearchData, position: Int) {

        val intent: Intent
        if (setterGetter.type == "person")
            intent = Intent(activity, CharacterDetailsActivity::class.java)
        else {
            intent = Intent(activity, MovieDetailsActivity::class.java)
            intent.putExtra("network_applicable", true)

        }
        intent.putExtra("title", setterGetter.movie)
        intent.putExtra("id", setterGetter.id)
        intent.putExtra("activity", false)

        startActivity(intent)
    }


    fun getSearchedResult(query: String) {


        val trimmedQuery = query.trim { it <= ' ' }
        val finalQuery = trimmedQuery.replace(" ", "-")


        val volleySingleton = VolleySingleton.instance
        val requestQueue = volleySingleton!!.requestQueue

        val BASE_URL = "https://api.themoviedb.org/3/search/movie?api_key=$api_key&query=$finalQuery"

        val jsonObjectRequest = JsonObjectRequest(BASE_URL, null,
                Response.Listener { response ->
                    //  Log.d("webi", response.toString());
                    parseSearchedOutput(response.toString())
                }, Response.ErrorListener {
            //Log.e("webi", "Volley Error: " + error.getCause());
        }
        )

        requestQueue.add(jsonObjectRequest)


    }

    private fun parseSearchedOutput(s: String) {

        val park = SearchResultParseWork(activity, s)
        val list = park.parsesearchdata()
        sadapter = SearchResultAdapter(activity, list)
        recycler!!.adapter = sadapter
        sadapter.setClickListener(this)

        hideProgress()
        hideSoftKeyboard()

    }


    fun showProgress() {


        if (breathingProgress != null && recycler != null) {

            breathingProgress!!.visibility = View.VISIBLE
            recycler!!.visibility = View.INVISIBLE

        }
    }


    fun hideProgress() {

        if (breathingProgress != null && recycler != null) {

            breathingProgress!!.visibility = View.INVISIBLE
            recycler!!.visibility = View.VISIBLE

        }
    }

    fun hideSoftKeyboard() {
        if (activity.currentFocus != null) {
            val inputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

}