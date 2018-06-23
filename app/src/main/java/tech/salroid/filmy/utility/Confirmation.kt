package tech.salroid.filmy.utility

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import java.util.HashMap

import tech.salroid.filmy.database.OfflineMovies


object Confirmation {

    fun confirmFav(context: Context, movieMap: HashMap<String, String>,
                   movie_id: String, movie_id_final: String, flagFavorite: Int) {

        AlertDialog.Builder(context)
                .setTitle("Favoris")
                .setMessage("Etes vous sur de vouloir ajouter ça à votre liste de favoris?")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    val offlineMovies = OfflineMovies(context)
                    offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagFavorite)
                }
                .setNegativeButton(android.R.string.no) { dialog, which -> dialog.cancel() }
                .show()

    }

    fun confirmWatchlist(context: Context, movieMap: HashMap<String, String>,
                         movie_id: String, movie_id_final: String,
                         flagWatchlist: Int) {

        AlertDialog.Builder(context)
                .setTitle("WatchList")
                .setIcon(null)
                .setMessage("Etes vous sur de vouloir ajouter ça à votre watchlist?")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    val offlineMovies = OfflineMovies(context)
                    offlineMovies.saveMovie(movieMap, movie_id, movie_id_final, flagWatchlist)
                }
                .setNegativeButton(android.R.string.no) { dialog, which -> dialog.cancel() }
                .show()

    }
}
