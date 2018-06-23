package tech.salroid.filmy.parser

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import tech.salroid.filmy.data_classes.SearchData


class SearchResultParseWork(private val context: Context, private val result: String) {
    private val getImage: String? = null
    private val getId: String? = null
    private val getString: String? = null
    private val getDate: String? = null
    private val getExtra: String? = null


    fun parsesearchdata(): List<SearchData> {
        val searchArray = ArrayList<SearchData>()
        var searchData: SearchData? = null


        try {


            val jsonobject = JSONObject(result)

            val jsonArray = jsonobject.getJSONArray("results")


            for (i in 0 until jsonArray.length()) {
                searchData = SearchData()

                val type_name: String
                val type: String
                val id: String
                val movie_poster: String
                val date: String
                val extra: String

                /* type = jsonArray.getJSONObject(i).getString("type");


                if (type.equals("person")) {
                    getString = "name";
                    getImage = "headshot";
                    getId = "tmdb";
                    getDate = "birthday";
                    getExtra = "birthplace";
                } else {
                    getString = "title";
                    getImage = "poster";
                    getId = "tmdb";
                    getDate = "released";
                    getExtra = "tagline";
                }

                type_name = jsonArray.getJSONObject(i).getJSONObject(type).getString(getString);
                id = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("ids").getString(getId);
                movie_poster = jsonArray.getJSONObject(i).getJSONObject(type).getJSONObject("images").getJSONObject(getImage).getString("thumb");
                date = jsonArray.getJSONObject(i).getJSONObject(type).getString(getDate);
                extra = jsonArray.getJSONObject(i).getJSONObject(type).getString(getExtra);*/

                id = jsonArray.getJSONObject(i).getString("id")
                date = jsonArray.getJSONObject(i).getString("release_date")
                movie_poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path")
                type_name = jsonArray.getJSONObject(i).getString("original_title")
                type = "movie"



                if (!(movie_poster == "null" && date == "null")) {
                    searchData!!.id = id
                    // searchData.setExtra(extra);
                    searchData!!.date = date
                    searchData!!.type = type
                    searchData!!.movie = type_name
                    searchData!!.poster = movie_poster
                    searchArray.add(searchData)
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return searchArray
    }


}
