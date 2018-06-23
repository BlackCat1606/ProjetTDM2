package tech.salroid.filmy.parser

import android.content.Context
import android.widget.Toast

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import tech.salroid.filmy.data_classes.CastDetailsData
import tech.salroid.filmy.data_classes.CrewDetailsData
import tech.salroid.filmy.data_classes.SimilarMoviesData

class MovieDetailsActivityParseWork(private val context: Context, private val result: String) {

    fun parse_cast(): List<CastDetailsData> {


        val setterGettercastArray = ArrayList<CastDetailsData>()
        var setterGettercast: CastDetailsData? = null

        try {
            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("cast")

            for (i in 0 until jsonArray.length()) {

                setterGettercast = CastDetailsData()

                val id = jsonArray.getJSONObject(i).getString("id")
                val character = jsonArray.getJSONObject(i).getString("character")
                val name = jsonArray.getJSONObject(i).getString("name")
                var cast_poster = jsonArray.getJSONObject(i).getString("profile_path")

                cast_poster = "http://image.tmdb.org/t/p/w185" + cast_poster


                setterGettercast.cast_character = character
                setterGettercast.cast_name = name
                setterGettercast.cast_profile = cast_poster
                setterGettercast.cast_id = id

                setterGettercastArray.add(setterGettercast)


            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


        return setterGettercastArray
    }

    fun parse_crew(): List<CrewDetailsData> {

        val setterGettercrewArray = ArrayList<CrewDetailsData>()
        var setterGetterCrew: CrewDetailsData? = null

        try {
            val jsonObject = JSONObject(result)

            val crewArray = jsonObject.getJSONArray("crew")

            for (i in 0 until crewArray.length()) {

                setterGetterCrew = CrewDetailsData()

                val crew_id = crewArray.getJSONObject(i).getString("id")
                val crew_job = crewArray.getJSONObject(i).getString("job")
                val crew_name = crewArray.getJSONObject(i).getString("name")
                val crew_poster = "http://image.tmdb.org/t/p/w185" + crewArray.getJSONObject(i)
                        .getString("profile_path")



                if (!crew_poster.contains("null")) {

                    //Toast.makeText(context,crew_job,Toast.LENGTH_SHORT).show();

                    setterGetterCrew.crew_id = crew_id
                    setterGetterCrew.crew_job = crew_job
                    setterGetterCrew.crew_name = crew_name
                    setterGetterCrew.crew_profile = crew_poster
                    setterGettercrewArray.add(setterGetterCrew)

                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


        return setterGettercrewArray
    }

    fun parse_similar_movies(): List<SimilarMoviesData> {

        val setterGettersimilarArray = ArrayList<SimilarMoviesData>()
        var setterGettersimilar: SimilarMoviesData? = null

        try {
            val jsonObject = JSONObject(result)

            val jsonArray = jsonObject.getJSONArray("results")

            for (i in 0 until jsonArray.length()) {

                setterGettersimilar = SimilarMoviesData()

                val id = jsonArray.getJSONObject(i).getString("id")
                val title = jsonArray.getJSONObject(i).getString("original_title")
                val movie_poster = "http://image.tmdb.org/t/p/w185" + jsonArray.getJSONObject(i).getString("poster_path")

                if (movie_poster.contains("null")) {

                } else {
                    setterGettersimilar.movie_id = id
                    setterGettersimilar.movie_banner = movie_poster
                    setterGettersimilar.movie_title = title

                    setterGettersimilarArray.add(setterGettersimilar)
                }

            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }




        return setterGettersimilarArray
    }

}
