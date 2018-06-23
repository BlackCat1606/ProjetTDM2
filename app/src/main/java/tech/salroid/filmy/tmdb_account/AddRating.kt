package tech.salroid.filmy.tmdb_account

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONException
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.util.HashMap
import java.util.Random

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton


class AddRating {

    private val api_key = BuildConfig.TMDB_API_KEY
    private val SESSION_PREF = "SESSION_PREFERENCE"
    private val tmdbVolleySingleton = TmdbVolleySingleton.instance
    private val tmdbrequestQueue = tmdbVolleySingleton!!.requestQueue
    private var context: Context? = null

    private var NOTIFICATION_ID: Int = 0
    private var mNotifyManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null


    fun addRating(context: Context, media_id: String, rating: Int) {

        this.context = context
        val sp = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        val session_id = sp.getString("session", " ")

        //issue notification to show indeterminate progress

        val random = Random()
        NOTIFICATION_ID = random.nextInt(10000)

        mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(context)
        mBuilder!!.setContentTitle("Filmy")
                .setContentText("Adding to ratings..")
                .setSmallIcon(R.drawable.ic_stat_status)
        mBuilder!!.setProgress(0, 0, true)
        mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())

        getProfile(session_id, media_id, rating)
    }


    private fun getProfile(session_id: String, media_id: String, rating: Int) {

        val PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=$api_key&session_id=$session_id"

        val jsonObjectRequest = JsonObjectRequest(PROFILE_URI, null,
                Response.Listener { response -> parseOutput(response, session_id, Integer.valueOf(media_id)!!, rating) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })

        tmdbrequestQueue.add(jsonObjectRequest)
    }

    private fun parseOutput(response: JSONObject, session_id: String, media_id: Int, rating: Int) {

        try {

            val account_id = response.getString("id")
            if (account_id != null) {

                val query = "https://api.themoviedb.org/3/movie/$media_id/rating?api_key=$api_key&session_id=$session_id"

                add(query, rating)


            } else {
                CustomToast.show(context, "You are not logged in. Please login from account.", false)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            CustomToast.show(context, "You are not logged in. Please login from account.", false)
        }

    }

    private fun add(query: String, rating: Int) {

        val jsonBody = JSONObject()
        try {

            jsonBody.put("value", rating)

        } catch (e: JSONException) {

            e.printStackTrace()
        }

        val mRequestBody = jsonBody.toString()


        val jsonObjectRequest = object : JsonObjectRequest(query, null,
                Response.Listener { response -> parseMarkedResponse(response) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) }) {

            override fun getBody(): ByteArray? {
                try {
                    return mRequestBody?.toByteArray(charset("utf-8"))
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8")
                    return null
                }

            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("content-type", "application/json;charset=utf-8")
                return headers
            }
        }

        tmdbrequestQueue.add(jsonObjectRequest)

    }


    private fun parseMarkedResponse(response: JSONObject) {


        try {
            val status_code = response.getInt("status_code")

            if (status_code == 1) {

                CustomToast.show(context, "Your rating has been added.", false)

                mBuilder!!.setContentText("Movie rating done.")
                        // Removes the progress bar
                        .setProgress(0, 0, false)
                mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
                mNotifyManager!!.cancel(NOTIFICATION_ID)

            } else if (status_code == 12) {

                CustomToast.show(context, "Rating updated.", false)

                mBuilder!!.setContentText("Rating updated.")
                        // Removes the progress bar
                        .setProgress(0, 0, false)
                mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
                mNotifyManager!!.cancel(NOTIFICATION_ID)
            }

        } catch (e: JSONException) {
            Log.d("webi", e.cause.toString())

            mBuilder!!.setContentText("Can't add rating.")
                    // Removes the progress bar
                    .setProgress(0, 0, false)
            mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
            mNotifyManager!!.cancel(NOTIFICATION_ID)

            CustomToast.show(context, "Can't add rating.", false)
        }

    }


}
