package tech.salroid.filmy.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Build
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

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MainActivity
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.MainActivityAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.MovieProjection



class InTheaters : Fragment(), LoaderManager.LoaderCallbacks<Cursor>, MainActivityAdapter.ClickListener {


    private var mainActivityAdapter: MainActivityAdapter? = null
    var isShowingFromDatabase: Boolean = false


    @BindView(R.id.breathingProgress)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.recycler)
    internal var recycler: RecyclerView? = null

    private var gridLayoutManager: StaggeredGridLayoutManager? = null
    private var isInMultiWindowMode: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_in_theaters, container, false)
        ButterKnife.bind(this, view)


        val tabletSize = resources.getBoolean(R.bool.isTablet)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            isInMultiWindowMode = activity.isInMultiWindowMode
        }

        if (tabletSize) {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = StaggeredGridLayoutManager(6,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler!!.layoutManager = gridLayoutManager

                } else {

                    gridLayoutManager = StaggeredGridLayoutManager(8,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler!!.layoutManager = gridLayoutManager

                }
            }

        } else {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                recycler!!.layoutManager = gridLayoutManager

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                    recycler!!.layoutManager = gridLayoutManager

                } else {

                    gridLayoutManager = StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
                    recycler!!.layoutManager = gridLayoutManager

                }
            }
        }

        mainActivityAdapter = MainActivityAdapter(activity, null)
        recycler!!.adapter = mainActivityAdapter
        mainActivityAdapter!!.setClickListener(this)

        return view

    }

    override fun onResume() {
        super.onResume()

        activity.supportLoaderManager.initLoader(MovieProjection.INTHEATERS_MOVIE_LOADER, null, this)

    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {

        val moviesForTheUri = FilmContract.InTheatersMoviesEntry.CONTENT_URI

        return CursorLoader(activity,
                moviesForTheUri,
                MovieProjection.MOVIE_COLUMNS, null, null, null)

    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (cursor != null && cursor.count > 0) {

            isShowingFromDatabase = true
            mainActivityAdapter!!.swapCursor(cursor)
            breathingProgress!!.visibility = View.GONE

        } else if (!(activity as MainActivity).fetchingFromNetwork) {

      /*      CustomToast.show(activity, "Failed to get In Theaters movies.", true)
            (activity as MainActivity).cantProceed(-1)*/

        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mainActivityAdapter!!.swapCursor(null)
    }


    override fun itemClicked(cursor: Cursor?) {

        val id_index = cursor!!.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
        val title_index = cursor.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)

        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("title", cursor.getString(title_index))
        intent.putExtra("activity", true)
        intent.putExtra("type", 1)
        //  intent.putExtra("update_id",true);
        intent.putExtra("database_applicable", true)
        intent.putExtra("network_applicable", true)
        intent.putExtra("id", cursor.getString(id_index))
        startActivity(intent)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            activity.overridePendingTransition(0, 0)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)

        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (isInMultiWindowMode)
                gridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            else
                gridLayoutManager = StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)

            recycler!!.layoutManager = gridLayoutManager
            recycler!!.adapter = mainActivityAdapter
        }

    }
}// Required empty public constructor