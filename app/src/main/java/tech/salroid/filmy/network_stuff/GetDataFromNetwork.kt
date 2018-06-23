package tech.salroid.filmy.network_stuff

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.util.Log

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONObject

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.FilmyApplication
import tech.salroid.filmy.R


class GetDataFromNetwork {
    private var mDataFetchedListener: DataFetchedListener? = null

    fun getMovieDetailsFromNetwork(movie_id: String) {

        val volleySingleton = VolleySingleton.instance
        val requestQueue = volleySingleton?.requestQueue


        //still here we will use the query builder
        //this String is not awesome.
        val api_key = BuildConfig.TMDB_API_KEY

        val BASE_URL_MOVIE_DETAILS= FilmyApplication.context.resources.getString(R.string.tmdb_movie_base_url)+ movie_id+ "?"+ "api_key=" + api_key+ "&append_to_response=trailers"



        val jsonObjectRequestForMovieDetails = JsonObjectRequest(BASE_URL_MOVIE_DETAILS, null,
                Response.Listener<JSONObject> { response -> mDataFetchedListener!!.dataFetched(response.toString(), MOVIE_DETAILS_CODE) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) }
        )

        requestQueue?.add(jsonObjectRequestForMovieDetails)


    }

    fun setDataFetchedListener(dataFetchedListener: DataFetchedListener) {

        this.mDataFetchedListener = dataFetchedListener

    }


    interface DataFetchedListener {

        fun dataFetched(response: String, type: Int)

    }

    companion object {

        val MOVIE_DETAILS_CODE = 1
        val CAST_CODE = 2
    }

}