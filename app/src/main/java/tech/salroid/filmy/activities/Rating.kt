package tech.salroid.filmy.activities

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.network_stuff.VolleySingleton




object Rating {

    var imdb_rating = "0"
    var tomatometer_rating = "0"
    var audience_rating = "0"
    var metascore_rating = "0"
    var image: String? = null
    var rottenTomatoPage: String? = null
    private val OMDB_API_KEY = BuildConfig.OMDB_API_KEY

    fun getRating(context: Context, movie_id_final: String) {

        val volleySingleton = VolleySingleton.instance
        val requestQueue = volleySingleton!!.requestQueue


        val BASE_RATING_URL = "http://www.omdbapi.com/?i=$movie_id_final&apikey=$OMDB_API_KEY&tomatoes=true&r=json"

        val jsonObjectRequestForMovieDetails = JsonObjectRequest(BASE_RATING_URL, null,
                Response.Listener { response ->
                    try {

                        val responseBool = response.getBoolean("Response")

                        if (responseBool) {

                            imdb_rating = response.getString("imdbRating")
                            tomatometer_rating = response.getString("tomatoRating")
                            audience_rating = response.getString("tomatoUserRating")
                            metascore_rating = response.getString("Metascore")
                            image = response.getString("tomatoImage")
                            rottenTomatoPage = response.getString("tomatoURL")


                            //Above tomatometer does not work this does
                            val jsonArray = response.getJSONArray("Ratings")
                            for (i in 0 until jsonArray.length()) {
                                if (jsonArray.getJSONObject(i).getString("Source") == "Rotten Tomatoes") {
                                    tomatometer_rating = jsonArray.getJSONObject(i).getString("Value")
                                }
                            }

                            setRatingCallback(context, imdb_rating, tomatometer_rating, audience_rating, metascore_rating, rottenTomatoPage!!)

                        } else
                            setRatingFailCallback(context)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        setRatingFailCallback(context)
                    }
                }, Response.ErrorListener {
            // Log.e("webi", "Volley Error: " + error.getCause());
            setRatingFailCallback(context)
        }
        )

        requestQueue.add(jsonObjectRequestForMovieDetails)

    }

    private fun setRatingFailCallback(context: Context) {
        (context as MovieDetailsActivity).setRatingGone()
    }


    private fun setRatingCallback(context: Context, imdb_rating: String, tomatometer_rating: String, audience_rating: String, metascore_rating: String, rottenTomatoPage: String) {
        (context as MovieDetailsActivity).setRating(imdb_rating, tomatometer_rating, audience_rating, metascore_rating, rottenTomatoPage)
    }

}
