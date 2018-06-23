package tech.salroid.filmy.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.speech.RecognizerIntent
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.miguelcatalan.materialsearchview.MaterialSearchView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.FilmyIntro
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.MyPagerAdapter
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.fragment.InTheaters
import tech.salroid.filmy.fragment.SavedMovies
import tech.salroid.filmy.fragment.SearchFragment
import tech.salroid.filmy.fragment.Trending
import tech.salroid.filmy.fragment.UpComing
import tech.salroid.filmy.network_stuff.FirstFetch
import tech.salroid.filmy.utility.FontUtility
import tech.salroid.filmy.utility.Network
import tech.salroid.filmy.utility.bindView
import tr.xip.errorview.ErrorView

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
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

class MainActivity : AppCompatActivity() {

    var fetchingFromNetwork: Boolean = false



    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val materialSearchView: MaterialSearchView by bindView(R.id.search_view)

    private val logo: TextView by bindView(R.id.logo)

    private val toolbarScroller: FrameLayout by bindView(R.id.toolbarScroller)

    private val viewPager: ViewPager by bindView(R.id.viewpager)

    private val tabLayout: TabLayout by bindView(R.id.tab_layout)

    private val mErrorView: ErrorView by bindView(R.id.error_view)

    private var trendingFragment: Trending? = null
    private var searchFragment: SearchFragment? = null
    private var cantProceed: Boolean = false
    private var nightMode: Boolean = false

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            val statusCode = intent.getIntExtra("message", 0)
            CustomToast.show(context, "Failed to get latest movies.", true)
            cantProceed(statusCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        if (supportActionBar != null)
            supportActionBar?.title = " "

        introLogic()

        val typeface = Typeface.createFromAsset(assets, FontUtility.fontName)
        logo.typeface = typeface

        if (nightMode)
            allThemeLogic()

        mErrorView.config = ErrorView.Config.create()
                .title(getString(R.string.error_title_damn))
                .titleColor(ContextCompat.getColor(this, R.color.dark))
                .subtitle(getString(R.string.error_details))
                .retryText(getString(R.string.error_retry))
                .build()


        mErrorView.setOnRetryListener {
            if (Network.isNetworkConnected(this@MainActivity)) {
                fetchingFromNetwork = true
                setScheduler()
            }
            canProceed()
        }


        mErrorView.visibility = View.GONE

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        materialSearchView.setVoiceSearch(true)
        materialSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic

                getSearchedResult(query)
                searchFragment!!.showProgress()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                //getSearchedResult(newText);
                return true

            }
        })

        materialSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic

                tabLayout.visibility = View.GONE

                disableToolbarScrolling()

                searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction().replace(R.id.search_container, searchFragment)
                        .commit()

            }

            override fun onSearchViewClosed() {
                //Do some magic


                supportFragmentManager
                        .beginTransaction()
                        .remove(searchFragment)
                        .commit()

                if (!cantProceed)
                    tabLayout.visibility = View.VISIBLE

                enableToolbarScrolling()

            }
        })


        if (Network.isNetworkConnected(this)) {

            fetchingFromNetwork = true
            setScheduler()
        }


    }


    private fun allThemeLogic() {

        tabLayout.setTabTextColors(Color.parseColor("#bdbdbd"), Color.parseColor("#e0e0e0"))
        logo.setTextColor(Color.parseColor("#E0E0E0"))
        materialSearchView.setBackgroundColor(resources.getColor(R.color.colorDarkThemePrimary))
        materialSearchView.setBackIcon(resources.getDrawable(R.drawable.ic_action_navigation_arrow_back_inverted))
        materialSearchView.setCloseIcon(resources.getDrawable(R.drawable.ic_action_navigation_close_inverted))
        materialSearchView.setTextColor(Color.parseColor("#ffffff"))

    }


    private fun introLogic() {

        val t = Thread(Runnable {
            val getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(baseContext)

            val isFirstStart = getPrefs.getBoolean("firstStart", true)

            if (isFirstStart) {

                val i = Intent(this@MainActivity, FilmyIntro::class.java)
                startActivity(i)
                val e = getPrefs.edit()
                e.putBoolean("firstStart", false)
                e.apply()
            }
        })
        t.start()

    }

    private fun setScheduler() {

        val firstFetch = FirstFetch(this)
        firstFetch.start()

    }

    fun cantProceed(status: Int) {

        Handler().postDelayed({
            if (trendingFragment != null && !trendingFragment!!.isShowingFromDatabase) {

                cantProceed = true

                tabLayout.visibility = View.GONE
                viewPager.visibility = View.GONE
                mErrorView.setError(status)
                mErrorView.visibility = View.VISIBLE

                //disable toolbar scrolling
                disableToolbarScrolling()
            }
        }, 1000)

    }

    fun canProceed() {

        cantProceed = false

        tabLayout.visibility = View.VISIBLE
        viewPager.visibility = View.VISIBLE
        mErrorView.visibility = View.GONE

        if (trendingFragment != null) {

            trendingFragment!!.retryLoading()

        }

        enableToolbarScrolling()

    }

    private fun disableToolbarScrolling() {
        val params = toolbarScroller.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
    }

    private fun enableToolbarScrolling() {

        val params = toolbarScroller.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS

    }

    private fun setupViewPager(viewPager: ViewPager?) {


        trendingFragment = Trending()
        val inTheatersFragment = InTheaters()
        val upComingFragment = UpComing()

        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(trendingFragment!!, getString(R.string.trending))
        adapter.addFragment(inTheatersFragment, getString(R.string.theatres))
        adapter.addFragment(upComingFragment, getString(R.string.upcoming))
        viewPager!!.adapter = adapter

    }

    private fun getSearchedResult(query: String) {

        if (searchFragment != null) {
            searchFragment!!.getSearchedResult(query)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {


        menuInflater.inflate(R.menu.main_menu, menu)

        val itemSearch = menu.findItem(R.id.action_search)
        val itemAccount = menu.findItem(R.id.ic_collections)

        if (nightMode) {
            itemSearch.setIcon(R.drawable.ic_action_action_search)
            itemAccount.setIcon(R.drawable.ic_action_collections_bookmark2)
        }

        materialSearchView.setMenuItem(itemSearch)
        return true


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_search -> startActivity(Intent(this, SearchFragment::class.java))
            R.id.ic_setting -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.ic_collections -> startActivity(Intent(this, CollectionsActivity::class.java))
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.setQuery(searchWrd, false)
                }
            }

            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (materialSearchView.isSearchOpen) {
            materialSearchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)

        if (nightMode != nightModeNew)
            recreate()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                IntentFilter("fetch-failed"))

    }

    override fun onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onPause()
    }


}