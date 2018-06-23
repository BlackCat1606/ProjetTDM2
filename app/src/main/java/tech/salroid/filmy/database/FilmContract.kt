package tech.salroid.filmy.database

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns


object FilmContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    val CONTENT_AUTHORITY = "tech.salroid.filmy"


    // Use CONTENT_AUTHORITY to create the base of all URI'FilmyAuthenticator which apps will use to contact
    // the content provider.


    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)


    val TRENDING_PATH_MOVIE = "trending_movie"
    val INTHEATERS_PATH_MOVIE = "intheaters_movie"
    val UPCOMING_PATH_MOVIE = "upcoming_movie"
    val PATH_CAST = "cast"
    val PATH_SAVE = "save"


    class MoviesEntry : BaseColumns {
        companion object {


            val TABLE_NAME = "trending"
            val MOVIE_ID = "movie_id"
            val MOVIE_YEAR = "movie_year"
            val MOVIE_POSTER_LINK = "movie_poster"
            val MOVIE_TITLE = "movie_title"
            val MOVIE_BANNER = "movie_banner"
            val MOVIE_DESCRIPTION = "movie_description"
            val MOVIE_TAGLINE = "movie_tagline"
            val MOVIE_TRAILER = "movie_trailer"
            val MOVIE_RATING = "movie_rating"
            val MOVIE_RUNTIME = "movie_runtime"
            val MOVIE_RELEASED = "movie_release"
            val MOVIE_CERTIFICATION = "movie_certification"
            val MOVIE_LANGUAGE = "movie_language"


            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRENDING_PATH_MOVIE).build()


            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE

            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRENDING_PATH_MOVIE


            fun buildMovieUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }

            fun buildMovieByTag(movieTag: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieTag).build()
            }

            fun buildMovieUriWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendQueryParameter(MOVIE_ID, movieId).build()
            }

            fun buildMovieWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieId).build()
            }

            fun getMovieIdFromUri(uri: Uri): String {
                return uri.pathSegments[1]
            }

            var _ID: Any?=null
        }
    }


    class InTheatersMoviesEntry : BaseColumns {
        companion object {

            val TABLE_NAME = "intheaters"
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(INTHEATERS_PATH_MOVIE).build()

            fun buildMovieUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }

            fun buildMovieByTag(movieTag: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieTag).build()
            }

            fun buildMovieWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieId).build()
            }

            fun getMovieIdFromUri(uri: Uri): String {
                return uri.pathSegments[1]
            }
        }

    }


    class UpComingMoviesEntry : BaseColumns {
        companion object {

            val TABLE_NAME = "upcoming"
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(UPCOMING_PATH_MOVIE).build()

            fun buildMovieUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }

            fun buildMovieByTag(movieTag: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieTag).build()
            }

            fun buildMovieWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieId).build()
            }

            fun getMovieIdFromUri(uri: Uri): String {
                return uri.pathSegments[1]
            }
        }

    }


    class CastEntry : BaseColumns {
        companion object {


            val TABLE_NAME = "cast"
            val CAST_MOVIE_ID = "cast_movie_id"
            val CAST_ID = "cast_id"
            val CAST_ROLE = "cast_role"
            val CAST_POSTER_LINK = "cast_poster"
            val CAST_NAME = "cast_name"


            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build()


            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST


            fun buildCastUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }


            fun buildCastUriByMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendQueryParameter(CAST_MOVIE_ID, movieId).build()
            }

            fun getMovieIdFromUri(uri: Uri): String {
                return uri.pathSegments[1]
            }

            var _ID: Any?=null
        }

    }


    class SaveEntry : BaseColumns {
        companion object {

            val TABLE_NAME = "save"
            val SAVE_ID = "save_id"
            val SAVE_YEAR = "save_year"
            val SAVE_POSTER_LINK = "save_poster"
            val SAVE_TITLE = "save_title"
            val SAVE_BANNER = "save_banner"
            val SAVE_DESCRIPTION = "save_description"
            val SAVE_TAGLINE = "save_tagline"
            val SAVE_TRAILER = "save_trailer"
            val SAVE_RATING = "save_rating"
            val SAVE_RUNTIME = "save_runtime"
            val SAVE_RELEASED = "save_release"
            val SAVE_CERTIFICATION = "save_certification"
            val SAVE_LANGUAGE = "save_language"
            val SAVE_FLAG = "save_flag"


            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVE).build()


            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVE


            fun buildMovieUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }

            fun buildMovieByTag(movieTag: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieTag).build()
            }

            fun buildMovieUriWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendQueryParameter(SAVE_ID, movieId).build()
            }

            fun buildMovieWithMovieId(movieId: String): Uri {
                return CONTENT_URI.buildUpon().appendPath(movieId).build()
            }

            fun getMovieIdFromUri(uri: Uri): String {
                return uri.pathSegments[1]
            }

            var _ID: Any?=null
        }

    }
}