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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import android.support.annotation.CallSuper



import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MainActivity
import tech.salroid.filmy.activities.MovieDetailsActivity
import tech.salroid.filmy.custom_adapter.MainActivityAdapter
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.customs.RecyclerviewEndlessScrollListener
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.MovieProjection
import tech.salroid.filmy.utility.bindView


/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class Trending : Fragment(), MainActivityAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    private val breathingProgress: BreathingProgress by bindView(R.id.breathingProgress)

    private lateinit var recycler: RecyclerView

    private val moreProgress: ProgressBar by bindView(R.id.moreProgress)

    private var mainActivityAdapter: MainActivityAdapter? = null
    var isShowingFromDatabase: Boolean = false
    private var gridLayoutManager: StaggeredGridLayoutManager? = null
    private var isInMultiWindowMode: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater?.inflate(R.layout.fragment_trending, container, false)
        recycler = view?.findViewById(R.id.recycler)!!
        ButterKnife.bind(this, view)

        val tabletSize = resources.getBoolean(R.bool.isTablet)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            isInMultiWindowMode = activity.isInMultiWindowMode
        }

        if (tabletSize) {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = StaggeredGridLayoutManager(6,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler.layoutManager = gridLayoutManager

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = StaggeredGridLayoutManager(6,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler.layoutManager = gridLayoutManager

                } else {

                    gridLayoutManager = StaggeredGridLayoutManager(8,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler.layoutManager = gridLayoutManager

                }

            }

        } else {

            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                gridLayoutManager = StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL)
                recycler.layoutManager = gridLayoutManager

            } else {

                if (isInMultiWindowMode) {

                    gridLayoutManager = StaggeredGridLayoutManager(3,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler.layoutManager = gridLayoutManager

                } else {

                    gridLayoutManager = StaggeredGridLayoutManager(5,
                            StaggeredGridLayoutManager.VERTICAL)
                    recycler.layoutManager = gridLayoutManager

                }
            }

        }

        mainActivityAdapter = MainActivityAdapter(activity, null)
        recycler.adapter = mainActivityAdapter
        mainActivityAdapter!!.setClickListener(this)

        /*recycler.addOnScrollListener(new RecyclerviewEndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                moreProgress.setVisibility(View.VISIBLE);
                Log.d("webi","Load more called.");
            }
        });*/

        return view
    }

    override fun onResume() {
        super.onResume()
        activity.supportLoaderManager.initLoader(MovieProjection.TRENDING_MOVIE_LOADER, Bundle(), this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {


        val moviesForTheUri = FilmContract.MoviesEntry.CONTENT_URI

        return CursorLoader(activity,
                moviesForTheUri,
                MovieProjection.MOVIE_COLUMNS, "", null, "")

    }


    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {

        if (cursor != null && cursor.count > 0) {

            isShowingFromDatabase = true
            mainActivityAdapter!!.swapCursor(cursor)
            breathingProgress.visibility = View.GONE

        } else if (!(activity as MainActivity).fetchingFromNetwork) {

            CustomToast.show(activity, "Failed to get latest movies.", true)
            (activity as MainActivity).cantProceed(-1)

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
        intent.putExtra("type", 0)
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

            recycler.layoutManager = gridLayoutManager
            recycler.adapter = mainActivityAdapter
        }

    }

    fun retryLoading() {
        activity.supportLoaderManager.restartLoader(MovieProjection.TRENDING_MOVIE_LOADER, null, this)
    }

}// Required empty public constructor