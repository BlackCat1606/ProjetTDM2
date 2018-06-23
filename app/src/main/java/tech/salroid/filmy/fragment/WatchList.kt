package tech.salroid.filmy.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest

import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.SavedMoviesAdapter
import tech.salroid.filmy.custom_adapter.WatchlistAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.data_classes.WatchlistData
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.WatchListMovieParseWork
import tech.salroid.filmy.tmdb_account.UnMarkingWatchList
import tech.salroid.filmy.utility.Constants

class WatchList : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, SavedMoviesAdapter.ClickListener, SavedMoviesAdapter.LongClickListener {

    @BindView(R.id.my_saved_recycler)
    internal var my_saved_movies_recycler: RecyclerView? = null
    @BindView(R.id.emptyContainer)
    internal var emptyContainer: LinearLayout? = null
    @BindView(R.id.database_image)
    internal var dataImageView: ImageView? = null

    private var mainActivityAdapter: SavedMoviesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_watch_movies, container, false)
        ButterKnife.bind(this, view)

        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        my_saved_movies_recycler.setLayoutManager(gridLayoutManager);*/

        val tabletSize = resources.getBoolean(R.bool.isTablet)

        if (tabletSize) {

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                val gridLayoutManager = StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL)
                my_saved_movies_recycler!!.layoutManager = gridLayoutManager
            } else {
                val gridLayoutManager = StaggeredGridLayoutManager(8,
                        StaggeredGridLayoutManager.VERTICAL)
                my_saved_movies_recycler!!.layoutManager = gridLayoutManager
            }

        } else {

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                val gridLayoutManager = StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL)
                my_saved_movies_recycler!!.layoutManager = gridLayoutManager
            } else {
                val gridLayoutManager = StaggeredGridLayoutManager(5,
                        StaggeredGridLayoutManager.VERTICAL)
                my_saved_movies_recycler!!.layoutManager = gridLayoutManager
            }

        }

        mainActivityAdapter = SavedMoviesAdapter(activity, null)
        my_saved_movies_recycler!!.adapter = mainActivityAdapter
        mainActivityAdapter!!.setClickListener(this)
        mainActivityAdapter!!.setLongClickListener(this)

        return view
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {

        val selection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + "= ?"
        val selectionArgs = arrayOf(Constants.FLAG_WATCHLIST.toString())

        return CursorLoader(activity, FilmContract.SaveEntry.CONTENT_URI, GET_SAVE_COLUMNS, selection, selectionArgs, "_ID DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {


        if (cursor != null && cursor.count > 0)
            mainActivityAdapter!!.swapCursor(cursor)
        else
            emptyContainer!!.visibility = View.VISIBLE


    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mainActivityAdapter!!.swapCursor(null)
        emptyContainer!!.visibility = View.VISIBLE
    }

    override fun itemClicked(movieId: String, title: String) {


        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("saved_database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("title", title)
        intent.putExtra("id", movieId)

        startActivity(intent)

    }


    override fun itemLongClicked(mycursor: Cursor?, position: Int) {


        val adb = AlertDialog.Builder(activity)
        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1)

        arrayAdapter.add("Remove")


        val context = activity

        adb.setAdapter(arrayAdapter) { dialogInterface, i ->
            val deleteSelection = FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_ID + " = ? AND " +
                    FilmContract.SaveEntry.TABLE_NAME + "." + FilmContract.SaveEntry.SAVE_FLAG + " = ? "

            val flag_index = mycursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_FLAG)
            val flag = mycursor.getInt(flag_index)

            val deletionArgs = arrayOf(mycursor.getString(mycursor.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)), flag.toString())

            val deletion_id = context.contentResolver.delete(FilmContract.SaveEntry.CONTENT_URI, deleteSelection, deletionArgs).toLong()

            if (!deletion_id.equals(-1)) {

                mainActivityAdapter!!.notifyItemRemoved(position)

                if (mainActivityAdapter!!.itemCount == 1)
                    my_saved_movies_recycler!!.visibility = View.GONE


            }
        }

        adb.show()

    }


    override fun onResume() {
        super.onResume()
        activity.supportLoaderManager.initLoader(SAVED_DETAILS_LOADER, null, this)
    }

    companion object {


        private val SAVED_DETAILS_LOADER = 5
        private val GET_SAVE_COLUMNS = arrayOf<String>(

                FilmContract.SaveEntry.SAVE_ID, FilmContract.SaveEntry.SAVE_TITLE, FilmContract.SaveEntry.SAVE_BANNER, FilmContract.SaveEntry.SAVE_DESCRIPTION, FilmContract.SaveEntry.SAVE_TAGLINE, FilmContract.SaveEntry.SAVE_TRAILER, FilmContract.SaveEntry.SAVE_RATING, FilmContract.SaveEntry.SAVE_LANGUAGE, FilmContract.SaveEntry.SAVE_RELEASED, FilmContract.SaveEntry._ID.toString(), FilmContract.SaveEntry.SAVE_YEAR, FilmContract.SaveEntry.SAVE_CERTIFICATION, FilmContract.SaveEntry.SAVE_RUNTIME, FilmContract.SaveEntry.SAVE_POSTER_LINK, FilmContract.SaveEntry.SAVE_FLAG)
    }
}
