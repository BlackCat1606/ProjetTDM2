package tech.salroid.filmy.parser

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import tech.salroid.filmy.data_classes.FavouriteData


class FavouriteMovieParseWork(private val context: Context, private val result: String) {

    fun parse_favourite(): List<FavouriteData> {
        val favouriteArray = ArrayList<FavouriteData>()
        var favouriteData: FavouriteData? = null


        try {


            val jsonobject = JSONObject(result)

            val jsonArray = jsonobject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {
                favouriteData = FavouriteData()

                val title: String
                val id: String
                val movie_poster: String

                id = jsonArray.getJSONObject(i).getString("id")
                movie_poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path")
                title = jsonArray.getJSONObject(i).getString("original_title")


                if (movie_poster != "null") {
                    favouriteData!!.fav_id = id
                    favouriteData!!.fav_title = title
                    favouriteData!!.fav_poster = movie_poster
                    favouriteArray.add(favouriteData)
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return favouriteArray
    }
}

