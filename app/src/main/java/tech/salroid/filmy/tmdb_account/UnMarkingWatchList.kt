package tech.salroid.filmy.tmdb_account

/**
 * Created by BlackCat on 6/17/2018.
 */
import android.content.Context
import android.content.SharedPreferences
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

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton


class UnMarkingWatchList {

    private val api_key = BuildConfig.TMDB_API_KEY
    private val SESSION_PREF = "SESSION_PREFERENCE"
    private val tmdbVolleySingleton = TmdbVolleySingleton.instance
    private val tmdbrequestQueue = tmdbVolleySingleton!!.requestQueue
    private var context: Context? = null
    private var listener: UnmarkedListener? = null
    private var position: Int = 0

    fun removeFromWatchList(context: Context, media_id: String, position: Int) {

        this.context = context
        this.position = position
        val sp = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        val session_id = sp.getString("session", " ")
        getProfile(session_id, media_id)

    }


    private fun getProfile(session_id: String, media_id: String) {

        val PROFILE_URI = "https://api.themoviedb.org/3/account?api_key=$api_key&session_id=$session_id"

        val jsonObjectRequest = JsonObjectRequest(PROFILE_URI, null,
                Response.Listener { response -> parseOutput(response, session_id, Integer.valueOf(media_id)!!) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })

        tmdbrequestQueue.add(jsonObjectRequest)
    }

    private fun parseOutput(response: JSONObject, session_id: String, media_id: Int) {

        try {

            val account_id = response.getString("id")
            if (account_id != null) {

                val query = "https://api.themoviedb.org/3/account/" + account_id + "/watchlist?api_key=" +
                        api_key + "&session_id=" + session_id

                removeWatchFinal(query, media_id)


            } else {
                CustomToast.show(context, "You are not logged in. Please login from account.", false)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            CustomToast.show(context, "You are not logged in. Please login from account.", false)
        }

    }

    private fun removeWatchFinal(query: String, media_id: Int) {

        val jsonBody = JSONObject()
        try {

            jsonBody.put("media_type", "movie")
            jsonBody.put("media_id", media_id)
            jsonBody.put("watchlist", false)

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

        if (listener != null)
            listener!!.unmarked(position)

        try {
            val status_code = response.getInt("status_code")

            if (status_code == 13) {
                CustomToast.show(context, "Movie removed from watchlist.", false)
            } else {
                CustomToast.show(context, "Can't remove from watchlist.", false)
            }

        } catch (e: JSONException) {
            Log.d("webi", e.cause.toString())
            CustomToast.show(context, "Can't remove from watchlist.", false)
        }

    }

    fun setUnmarkedListener(listener: UnmarkedListener) {
        this.listener = listener
    }

    interface UnmarkedListener {
        fun unmarked(position: Int)
    }

}
