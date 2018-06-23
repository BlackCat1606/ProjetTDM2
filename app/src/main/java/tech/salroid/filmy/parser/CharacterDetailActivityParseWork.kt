package tech.salroid.filmy.parser

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import tech.salroid.filmy.data_classes.CharacterDetailsData

class CharacterDetailActivityParseWork(private val context: Context, private val char_result: String) {


    fun char_parse_cast(): List<CharacterDetailsData> {

        val setterGettercharArray = ArrayList<CharacterDetailsData>()
        var setterGetterchar: CharacterDetailsData? = null


        try {
            val jsonObject = JSONObject(char_result)

            val jsonArray = jsonObject.getJSONArray("cast")

            for (i in 0 until jsonArray.length()) {

                setterGetterchar = CharacterDetailsData()

                val role: String
                val movie: String
                val mov_id: String
                val img: String

                role = jsonArray.getJSONObject(i).getString("character")
                movie = jsonArray.getJSONObject(i).getString("original_title")
                mov_id = jsonArray.getJSONObject(i).getString("id")
                img = "http://image.tmdb.org/t/p/w45" + jsonArray.getJSONObject(i).getString("poster_path")

                setterGetterchar.char_movie = movie
                setterGetterchar.char_id = mov_id
                setterGetterchar.char_role = role
                setterGetterchar.charmovie_img = img

                setterGettercharArray.add(setterGetterchar)


                /*  int year= jsonArray.getJSONObject(i).getJSONObject("movie").getInt("year");
                setterGetterchar.setChar_date(year);*/

            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }


        return setterGettercharArray
    }


}
