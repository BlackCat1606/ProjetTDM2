package tech.salroid.filmy.database

import android.content.ContentValues
import android.content.Context

import java.util.HashMap


object MovieDetailsUpdation {

    fun performMovieDetailsUpdation(context: Context, type: Int, movieMap: HashMap<String, String>, movie_id: String) {


        val contentValues = ContentValues()

        contentValues.put(FilmContract.MoviesEntry.MOVIE_BANNER, movieMap["banner"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TAGLINE, movieMap["tagline"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_DESCRIPTION, movieMap["overview"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_TRAILER, movieMap["trailer_img"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_CERTIFICATION, movieMap["certification"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_LANGUAGE, movieMap["language"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RUNTIME, movieMap["runtime"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RELEASED, movieMap["released"])
        contentValues.put(FilmContract.MoviesEntry.MOVIE_RATING, movieMap["rating"])

        when (type) {

            0 -> {

                val selection = FilmContract.MoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs = arrayOf(movie_id)

                val id = context.contentResolver.update(FilmContract.MoviesEntry.buildMovieByTag(movie_id), contentValues, selection, selectionArgs).toLong()

                if (!id.equals(-1)) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }
            }

            1 -> {

                val selection2 = FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs2 = arrayOf(movie_id)

                val id2 = context.contentResolver.update(FilmContract.InTheatersMoviesEntry.buildMovieByTag(movie_id), contentValues, selection2, selectionArgs2).toLong()

                if (!id2.equals(-1)) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }
            }

            2 -> {


                val selection3 = FilmContract.UpComingMoviesEntry.TABLE_NAME +
                        "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
                val selectionArgs3 = arrayOf(movie_id)

                val id3 = context.contentResolver.update(FilmContract.UpComingMoviesEntry.buildMovieByTag(movie_id), contentValues, selection3, selectionArgs3).toLong()

                if (!id3.equals(-1)) {
                    //  Log.d(LOG_TAG, "Movie row updated with new values.");
                }
            }
        }


    }
}
