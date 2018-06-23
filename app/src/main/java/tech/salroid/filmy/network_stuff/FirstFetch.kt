package tech.salroid.filmy.network_stuff

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONObject

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.parser.MainActivityParseWork
import tech.salroid.filmy.services.FilmyJobScheduler


class FirstFetch(private val context: Context) {
    internal var tmdbVolleySingleton = TmdbVolleySingleton.instance
    internal var tmdbrequestQueue = tmdbVolleySingleton?.requestQueue

    fun start() {

        syncNowTrending()
        syncNowInTheaters()
        syncNowUpComing()

        val filmyJobScheduler = FilmyJobScheduler(context)
        filmyJobScheduler.createJob()
    }


    private fun syncNowInTheaters() {


        val api_key = BuildConfig.TMDB_API_KEY

        val Intheatres_Base_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + api_key

        val IntheatresJsonObjectRequest = JsonObjectRequest(Intheatres_Base_URL, null,
                Response.Listener { response -> intheatresparseOutput(response.toString(), 2) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })


        tmdbrequestQueue?.add(IntheatresJsonObjectRequest)

    }

    private fun syncNowUpComing() {


        val api_key = BuildConfig.TMDB_API_KEY
        val Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + api_key

        val UpcomingJsonObjectRequest = JsonObjectRequest(Upcoming_Base_URL, null,
                Response.Listener { response -> upcomingparseOutput(response.toString()) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })

        tmdbrequestQueue?.add(UpcomingJsonObjectRequest)

    }

    private fun syncNowTrending() {

        val api_key = BuildConfig.TMDB_API_KEY
        val BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + api_key

        val jsonObjectRequest = JsonObjectRequest(BASE_URL, null,
                Response.Listener { response -> parseOutput(response.toString()) }, Response.ErrorListener { error ->
            val networkResponse = error.networkResponse
            if (networkResponse != null) {
                sendFetchFailedMessage(networkResponse.statusCode)
            } else {

                sendFetchFailedMessage(0)

            }
        }
        )

        tmdbrequestQueue?.add(jsonObjectRequest)

    }

    private fun intheatresparseOutput(s: String, type: Int) {

        val pa = MainActivityParseWork(context, s)
        pa.intheatres()

    }

    private fun upcomingparseOutput(result_upcoming: String) {
        val pa = MainActivityParseWork(context, result_upcoming)
        pa.parseupcoming()
    }


    private fun parseOutput(result: String) {

        val pa = MainActivityParseWork(context, result)
        pa.parse()
    }

    private fun sendFetchFailedMessage(message: Int) {

        val intent = Intent("fetch-failed")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

    }


}
