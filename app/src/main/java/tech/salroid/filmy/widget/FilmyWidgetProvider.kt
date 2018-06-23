package tech.salroid.filmy.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.target.SimpleTarget

import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.MovieProjection


class FilmyWidgetProvider : AppWidgetProvider() {

    private var appWidgetTarget: AppWidgetTarget? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {


        val N = appWidgetIds.size

        var movie_id = " "
        var movie_title = " "
        var movie_poster = " "

        for (i in 0 until N) {

            val appWidgetId = appWidgetIds[i]

            val moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI
            val selection: String? = null
            val selectionArgs: Array<String>? = null
            val cursor = context.contentResolver.query(moviesForTheUri, MovieProjection.MOVIE_COLUMNS, selection, selectionArgs, null)

            if (cursor != null && cursor.count > 0) {

                cursor.moveToFirst()

                val id_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
                val title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
                val poster_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
                val year_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR)

                movie_id = cursor.getString(id_index)
                movie_title = cursor.getString(title_index)
                movie_poster = cursor.getString(poster_index)
                //String imdb_id = cursor.getString(id_index);
                //int movie_year = cursor.getInt(year_index);


            } else {

            }

            cursor!!.close()

            val remoteViews = RemoteViews(context.packageName, R.layout.filmy_appwidget)
            remoteViews.setTextViewText(R.id.widget_movie_name, movie_title)

            appWidgetTarget = AppWidgetTarget(context, remoteViews, R.id.widget_movie_image, *appWidgetIds)


            Glide.with(context)
                    .load(movie_poster)
                    .asBitmap()
                    .into(appWidgetTarget!!)


            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra("title", movie_title)
            intent.putExtra("activity", true)
            intent.putExtra("type", -1)
            intent.putExtra("database_applicable", false)
            intent.putExtra("network_applicable", true)
            intent.putExtra("id", movie_id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.activity_opener, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)

        }
    }


}