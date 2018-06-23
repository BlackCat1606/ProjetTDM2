package tech.salroid.filmy.services

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONObject

import me.tatarka.support.job.JobParameters
import me.tatarka.support.job.JobService
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.MainActivityParseWork


class FilmyJobService : JobService() {


    internal var tmdbVolleySingleton = TmdbVolleySingleton.instance
    internal var tmdbrequestQueue = tmdbVolleySingleton?.requestQueue
    private var jobParameters: JobParameters? = null

    private var taskFinished: Int = 0
    private val api_key = BuildConfig.TMDB_API_KEY


    override fun onStartJob(params: JobParameters): Boolean {

        jobParameters = params

        syncNowTrending()
        syncNowInTheaters()
        syncNowUpComing()

        return true
    }


    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }


    private fun syncNowInTheaters() {


        val Intheatres_Base_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + api_key

        val IntheatresJsonObjectRequest = JsonObjectRequest(Intheatres_Base_URL, null,
                Response.Listener { response ->
                    intheatresparseOutput(response.toString(), 2)

                    taskFinished++

                    if (taskFinished == 3) {

                        jobFinished(jobParameters, false)
                        taskFinished = 0
                    }
                }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })


        tmdbrequestQueue?.add(IntheatresJsonObjectRequest)

    }

    private fun syncNowUpComing() {


        val Upcoming_Base_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + api_key

        val UpcomingJsonObjectRequest = JsonObjectRequest(Upcoming_Base_URL, null,
                Response.Listener { response ->
                    upcomingparseOutput(response.toString())

                    taskFinished++

                    if (taskFinished == 3) {
                        jobFinished(jobParameters, false)
                        taskFinished = 0
                    }
                }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })

        tmdbrequestQueue?.add(UpcomingJsonObjectRequest)

    }

    private fun syncNowTrending() {


        val BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + api_key

        val jsonObjectRequest = JsonObjectRequest(BASE_URL, null,
                Response.Listener { response ->
                    parseOutput(response.toString())

                    taskFinished++
                    if (taskFinished == 3) {
                        jobFinished(jobParameters, false)
                        taskFinished = 0
                    }
                }, Response.ErrorListener { error ->
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

        val pa = MainActivityParseWork(this, s)
        pa.intheatres()

    }

    private fun upcomingparseOutput(result_upcoming: String) {
        val pa = MainActivityParseWork(this, result_upcoming)
        pa.parseupcoming()
    }


    private fun parseOutput(result: String) {

        val pa = MainActivityParseWork(this, result)
        pa.parse()
    }

    private fun sendFetchFailedMessage(message: Int) {

        val intent = Intent("fetch-failed")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }


}