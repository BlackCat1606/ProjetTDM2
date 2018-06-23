package tech.salroid.filmy.parser

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat

import com.google.gson.Gson

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Random
import java.util.Vector

import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.detail.ItemResult
import tech.salroid.filmy.services.AlarmReceiver

class MainActivityParseWork(// private final int type;
        private val context: Context, private val result: String)// this.type=type;
{

    private val LOG_TAG = MainActivityParseWork::class.java.simpleName
    private val imdb_id: String? = null

    fun parse() {

        //final List<MovieData> movieDataArrayList = new ArrayList<MovieData>();
        //MovieData movieData = null;

        try {

            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {

                //movieData = new MovieData();
                val title: String
                val poster: String
                val id: String

                title = jsonArray.getJSONObject(i).getString("title")
                poster = jsonArray.getJSONObject(i).getString("poster_path")
                id = jsonArray.getJSONObject(i).getString("id")

                val temp_year = jsonArray.getJSONObject(i).getString("release_date").split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val year = temp_year[0]


                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }

                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year


                //movieData.setMovie(title);
                //movieData.setYear(year);
                //movieData.setId(id);
                //movieData.setPoster(poster);

                //movieDataArrayList.add(movieData);

                // Insert the new weather information into the database

                val movieValues = ContentValues()

                if (poster != "null") {


                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster)


                    cVVector.add(movieValues)
                }

            }
            var inserted = 0
            // add to database
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toTypedArray()

                context.contentResolver.delete(FilmContract.MoviesEntry.CONTENT_URI, null, null)
                inserted = context.contentResolver.bulkInsert(FilmContract.MoviesEntry.CONTENT_URI, cvArray)

            }

            //Log.d(LOG_TAG, "Fetching Complete. " + inserted + " Inserted");


        } catch (e1: JSONException) {
            e1.printStackTrace()
        }

        return
    }

    fun parseupcoming() {

        try {

            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results")

            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {


                val title: String
                val poster: String
                val id: String

                title = jsonArray.getJSONObject(i).getString("title")
                poster = jsonArray.getJSONObject(i).getString("poster_path")
                id = jsonArray.getJSONObject(i).getString("id")

                val temp_year = jsonArray.getJSONObject(i).getString("release_date").split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val year = temp_year[0]


                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }

                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year


                // Insert the new weather information into the database

                val movieValues = ContentValues()

                if (poster != "null") {


                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster)




                    cVVector.add(movieValues)
                }
            }
            var inserted = 0
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toTypedArray()

                context.contentResolver.delete(FilmContract.UpComingMoviesEntry.CONTENT_URI, null, null)
                inserted = context.contentResolver.bulkInsert(FilmContract.UpComingMoviesEntry.CONTENT_URI, cvArray)

            }


        } catch (e1: JSONException) {
            e1.printStackTrace()
        }

        return
    }


    fun intheatres() {
        try {


            val jsonObject = JSONObject(result)

            val jsonArray = jsonObject.getJSONArray("results")

            val cVVector = Vector<ContentValues>(jsonArray.length())

            for (i in 0 until jsonArray.length()) {

                //movieData = new MovieData();
                val title: String
                val poster: String
                val id: String
                title = jsonArray.getJSONObject(i).getString("title")
                poster = jsonArray.getJSONObject(i).getString("poster_path")
                id = jsonArray.getJSONObject(i).getString("id")


                val temp_year = jsonArray.getJSONObject(i).getString("release_date").split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val year = temp_year[0]

                val trimmedQuery = title.toLowerCase().trim { it <= ' ' }
                var finalQuery = trimmedQuery.replace(" ", "-")
                finalQuery = finalQuery.replace("'", "-")
                val slug = finalQuery.replace(":", "") + "-" + year

                if (poster != "null") {

                    val movieValues = ContentValues()

                    movieValues.put(FilmContract.MoviesEntry.MOVIE_ID, id)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_TITLE, title)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_YEAR, year)
                    movieValues.put(FilmContract.MoviesEntry.MOVIE_POSTER_LINK, "http://image.tmdb.org/t/p/w185" + poster)


                    val notifId = 200

                    val item = ItemResult()
                    showNotification(context, title, year, notifId, item, Integer.valueOf(id)!!)


                    cVVector.add(movieValues)
                }
            }
            var inserted = 0
            // add to database
            if (cVVector.size > 0) {
                val cvArray = arrayOfNulls<ContentValues>(cVVector.size)
                cVVector.toTypedArray()

                context.contentResolver.delete(FilmContract.InTheatersMoviesEntry.CONTENT_URI, null, null)
                inserted = context.contentResolver.bulkInsert(FilmContract.InTheatersMoviesEntry.CONTENT_URI, cvArray)

            }


        } catch (e1: JSONException) {
            e1.printStackTrace()
        }

        return

    }

    private fun showNotification(context: Context, title: String, message: String, notifId: Int, item: ItemResult, id: Int) {
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MovieDetailsActivity::class.java)
        intent.putExtra("network_applicable", false)
        intent.putExtra("database_applicable", false)
        intent.putExtra("saved_database_applicable", false)
        intent.putExtra("type", 0)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        val pendingIntent = PendingIntent.getActivity(context, notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

        notificationManagerCompat.notify(notifId, builder.build())
    }
}
