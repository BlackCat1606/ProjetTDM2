package tech.salroid.filmy.providers

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.annotation.TargetApi
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.FilmDbHelper


class FilmProvider : ContentProvider() {
    private var mOpenHelper: FilmDbHelper? = null

    override fun onCreate(): Boolean {

        mOpenHelper = FilmDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        val retCursor: Cursor

        when (sUriMatcher.match(uri)) {

            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                retCursor = getTrendingMovieDetailsByMovieId(uri, projection, sortOrder)
            }


            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                retCursor = getInTheatersMovieDetailsByMovieId(uri, projection, sortOrder)
            }

            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> {
                retCursor = getUpcomingMovieDetailsByMovieId(uri, projection, sortOrder)
            }


            CAST_WITH_MOVIE_ID -> {
                retCursor = getCastByMovieID(uri, projection, sortOrder)
            }

            TRENDING_MOVIE -> {
                retCursor = mOpenHelper!!.getReadableDatabase().query(
                        FilmContract.MoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }
            INTHEATERS_MOVIE -> {
                retCursor = mOpenHelper!!.getReadableDatabase().query(
                        FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }
            UPCOMING_MOVIE -> {
                retCursor = mOpenHelper!!.getReadableDatabase().query(
                        FilmContract.UpComingMoviesEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }

            CAST -> {
                retCursor = mOpenHelper!!.getReadableDatabase().query(
                        FilmContract.CastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }

            SAVE -> {
                retCursor = mOpenHelper!!.getReadableDatabase().query(
                        FilmContract.SaveEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder
                )
            }

            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
        retCursor.setNotificationUri(context!!.contentResolver, uri)
        return retCursor
    }

    private fun getTrendingMovieDetailsByMovieId(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {

        val movieId = FilmContract.MoviesEntry.getMovieIdFromUri(uri)

        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf<String>(movieId)

        return mOpenHelper!!.getReadableDatabase().query(
                FilmContract.MoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder
        )

    }

    private fun getInTheatersMovieDetailsByMovieId(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {

        val movieId = FilmContract.InTheatersMoviesEntry.getMovieIdFromUri(uri)

        val selection = sInTheatersMovieIdSelection
        val selectionArgs = arrayOf<String>(movieId)

        return mOpenHelper!!.getReadableDatabase().query(
                FilmContract.InTheatersMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder
        )

    }

    private fun getUpcomingMovieDetailsByMovieId(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {

        val movieId = FilmContract.UpComingMoviesEntry.getMovieIdFromUri(uri)

        val selection = sUpcomingMovieIdSelection
        val selectionArgs = arrayOf<String>(movieId)

        return mOpenHelper!!.getReadableDatabase().query(
                FilmContract.UpComingMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder
        )

    }

    private fun getCastByMovieID(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {

        val movieId = FilmContract.CastEntry.getMovieIdFromUri(uri)

        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf<String>(movieId)

        return mOpenHelper!!.getReadableDatabase().query(
                FilmContract.CastEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder
        )

    }

    private fun getSaveByMovieID(uri: Uri, projection: Array<String>, sortOrder: String): Cursor {

        val movieId = FilmContract.SaveEntry.getMovieIdFromUri(uri)

        val selection = sTrendingMovieIdSelection
        val selectionArgs = arrayOf<String>(movieId)

        return mOpenHelper!!.getReadableDatabase().query(
                FilmContract.SaveEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder
        )

    }

    override fun getType(uri: Uri): String? {

        // Use the Uri Matcher to determine what kind of URI this is.
        val match = sUriMatcher.match(uri)

        when (match) {

            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE

            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE

            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> return FilmContract.MoviesEntry.CONTENT_ITEM_TYPE


            CAST_WITH_MOVIE_ID -> return FilmContract.CastEntry.CONTENT_TYPE

            TRENDING_MOVIE -> return FilmContract.MoviesEntry.CONTENT_TYPE

            INTHEATERS_MOVIE -> return FilmContract.MoviesEntry.CONTENT_TYPE

            UPCOMING_MOVIE -> return FilmContract.MoviesEntry.CONTENT_TYPE

            CAST -> return FilmContract.CastEntry.CONTENT_TYPE
            SAVE -> return FilmContract.SaveEntry.CONTENT_TYPE
            SAVE_DETAILS_WITH_MOVIE_ID -> return FilmContract.SaveEntry.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {

        val db = mOpenHelper!!.getWritableDatabase()
        val match = sUriMatcher.match(uri)
        val returnUri: Uri

        when (match) {
            TRENDING_MOVIE -> {

                val _id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, contentValues)
                if (_id > 0)
                    returnUri = FilmContract.MoviesEntry.buildMovieUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into " + uri)


            }

            INTHEATERS_MOVIE -> {

                val _id = db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, contentValues)
                if (_id > 0)
                    returnUri = FilmContract.InTheatersMoviesEntry.buildMovieUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into " + uri)


            }

            UPCOMING_MOVIE -> {

                val _id = db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, contentValues)
                if (_id > 0)
                    returnUri = FilmContract.UpComingMoviesEntry.buildMovieUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into " + uri)


            }

            CAST -> {

                val _id = db.insert(FilmContract.CastEntry.TABLE_NAME, null, contentValues)
                if (_id > 0)
                    returnUri = FilmContract.CastEntry.buildCastUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into " + uri)
            }

            SAVE -> {

                val _id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, contentValues)
                if (_id > 0)
                    returnUri = FilmContract.SaveEntry.buildMovieUri(_id)
                else
                    throw android.database.SQLException("Failed to insert row into " + uri)


            }

            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
        context!!.contentResolver.notifyChange(uri, null)
        return returnUri

    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection

        val db = mOpenHelper!!.getWritableDatabase()

        val match = sUriMatcher.match(uri)
        val rowsDeleted: Int

        if (selection == null) selection = "1"

        when (match) {

            TRENDING_MOVIE ->

                rowsDeleted = db.delete(FilmContract.MoviesEntry.TABLE_NAME, selection, selectionArgs)

            INTHEATERS_MOVIE ->

                rowsDeleted = db.delete(FilmContract.InTheatersMoviesEntry.TABLE_NAME, selection, selectionArgs)

            UPCOMING_MOVIE ->

                rowsDeleted = db.delete(FilmContract.UpComingMoviesEntry.TABLE_NAME, selection, selectionArgs)

            CAST ->

                rowsDeleted = db.delete(FilmContract.CastEntry.TABLE_NAME, selection, selectionArgs)
            SAVE ->

                rowsDeleted = db.delete(FilmContract.SaveEntry.TABLE_NAME, selection, selectionArgs)

            else -> throw UnsupportedOperationException("Unknown uri = " + uri)
        }

        if (rowsDeleted != 0)
            context!!.contentResolver.notifyChange(uri, null)

        return rowsDeleted
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {

        val db = mOpenHelper!!.getWritableDatabase()

        val match = sUriMatcher.match(uri)
        val rowsUpdated: Int

        when (match) {

            TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID -> rowsUpdated = db.update(FilmContract.MoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs)

            INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID -> rowsUpdated = db.update(FilmContract.InTheatersMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs)
            UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID -> rowsUpdated = db.update(FilmContract.UpComingMoviesEntry.TABLE_NAME, contentValues, selection, selectionArgs)

            SAVE_DETAILS_WITH_MOVIE_ID -> rowsUpdated = db.update(FilmContract.SaveEntry.TABLE_NAME, contentValues, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri = " + uri)
        }

        if (rowsUpdated != 0)
            context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.getWritableDatabase()
        val match = sUriMatcher.match(uri)
        when (match) {
            TRENDING_MOVIE -> {
                db.beginTransaction()
                var returnCount = 0
                try {
                    for (value in values) {
                        val _id = db.insert(FilmContract.MoviesEntry.TABLE_NAME, null, value)
                        if (!_id.equals(-1)) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                return returnCount
            }


            INTHEATERS_MOVIE -> {
                db.beginTransaction()
                var returnCount1 = 0
                try {
                    for (value in values) {
                        val _id = db.insert(FilmContract.InTheatersMoviesEntry.TABLE_NAME, null, value)
                        if (!_id.equals(-1)) {
                            returnCount1++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                return returnCount1
            }


            UPCOMING_MOVIE -> {
                db.beginTransaction()
                var returnCount2 = 0
                try {
                    for (value in values) {
                        val _id = db.insert(FilmContract.UpComingMoviesEntry.TABLE_NAME, null, value)
                        if (!_id.equals(-1)) {
                            returnCount2++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                return returnCount2
            }

            SAVE -> {
                db.beginTransaction()
                var SaveReturnCount = 0
                try {
                    for (value in values) {
                        val _id = db.insert(FilmContract.SaveEntry.TABLE_NAME, null, value)
                        if (!_id.equals(-1)) {
                            SaveReturnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                context!!.contentResolver.notifyChange(uri, null)
                return SaveReturnCount
            }

            else -> return super.bulkInsert(uri, values)
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @TargetApi(11)
    override fun shutdown() {
        mOpenHelper!!.close()
        super.shutdown()
    }

    companion object {

        internal val TRENDING_MOVIE = 100
        internal val INTHEATERS_MOVIE = 400
        internal val UPCOMING_MOVIE = 500
        internal val TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID = 101
        internal val INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID = 401
        internal val UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID = 501
        internal val CAST_WITH_MOVIE_ID = 102
        internal val CAST = 300
        internal val SAVE = 200
        internal val SAVE_DETAILS_WITH_MOVIE_ID = 201

        // The URI Matcher used by this content provider.
        private val sUriMatcher = buildUriMatcher()

        private val sTrendingMovieIdSelection = FilmContract.MoviesEntry.TABLE_NAME +
                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private val sInTheatersMovieIdSelection = FilmContract.InTheatersMoviesEntry.TABLE_NAME +
                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private val sUpcomingMovieIdSelection = FilmContract.UpComingMoviesEntry.TABLE_NAME +
                "." + FilmContract.MoviesEntry.MOVIE_ID + " = ? "
        private val sMovieIdSelectionForCast = FilmContract.CastEntry.TABLE_NAME +
                "." + FilmContract.CastEntry.CAST_MOVIE_ID + " = ? "

        internal fun buildUriMatcher(): UriMatcher {
            // 1) The code passed into the constructor represents the code to return for the root
            // URI.  It'FilmyAuthenticator common to use NO_MATCH as the code for this case. Add the constructor below.

            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = FilmContract.CONTENT_AUTHORITY

            // 2) Use the addURI function to match each of the types.  Use the constants from
            // WeatherContract to help define the types to the UriMatcher.

            uriMatcher.addURI(authority, FilmContract.TRENDING_PATH_MOVIE, TRENDING_MOVIE)
            uriMatcher.addURI(authority, FilmContract.TRENDING_PATH_MOVIE + "/*", TRENDING_MOVIE_DETAILS_WITH_MOVIE_ID)

            uriMatcher.addURI(authority, FilmContract.INTHEATERS_PATH_MOVIE, INTHEATERS_MOVIE)
            uriMatcher.addURI(authority, FilmContract.INTHEATERS_PATH_MOVIE + "/*", INTHEATERS_MOVIE_DETAILS_WITH_MOVIE_ID)

            uriMatcher.addURI(authority, FilmContract.UPCOMING_PATH_MOVIE, UPCOMING_MOVIE)
            uriMatcher.addURI(authority, FilmContract.UPCOMING_PATH_MOVIE + "/*", UPCOMING_MOVIE_DETAILS_WITH_MOVIE_ID)

            uriMatcher.addURI(authority, FilmContract.PATH_CAST + "/*", CAST_WITH_MOVIE_ID)
            uriMatcher.addURI(authority, FilmContract.PATH_CAST, CAST)
            uriMatcher.addURI(authority, FilmContract.PATH_SAVE, SAVE)
            uriMatcher.addURI(authority, FilmContract.PATH_SAVE + "/*", SAVE_DETAILS_WITH_MOVIE_ID)

            // 3) Return the new matcher!


            return uriMatcher
        }
    }


}