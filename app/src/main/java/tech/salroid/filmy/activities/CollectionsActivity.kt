package tech.salroid.filmy.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.appindexing.Thing
import com.google.android.gms.common.api.GoogleApiClient

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.MyPagerAdapter
import tech.salroid.filmy.fragment.Favorite
import tech.salroid.filmy.fragment.SavedMovies
import tech.salroid.filmy.fragment.WatchList
import tech.salroid.filmy.utility.FontUtility



class CollectionsActivity : AppCompatActivity() {

    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.logo)
    internal var logo: TextView? = null
    @BindView(R.id.viewpager)
    internal var viewPager: ViewPager? = null
    @BindView(R.id.tab_layout)
    internal var tabLayout: TabLayout? = null
    @BindView(R.id.header)
    internal var loginHeader: FrameLayout? = null
    @BindView(R.id.username)
    internal var tvUserName: TextView? = null
    @BindView(R.id.favContainer)
    internal var favourite_layout: FrameLayout? = null
    @BindView(R.id.watchlistContainer)
    internal var watchlist_layout: FrameLayout? = null
    private var client: GoogleApiClient? = null
    private var throughShortcut: Boolean? = null


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // TODO: Define a title for the content shown.
    // TODO: Make sure this auto-generated URL is correct.
    val indexApiAction: Action
        get() {
            val `object` = Thing.Builder()
                    .setName("Account Page")
                    .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                    .build()
            return Action.Builder(Action.TYPE_VIEW)
                    .setObject(`object`)
                    .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                    .build()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val spref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightMode = spref.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        setContentView(R.layout.activity_collections)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.title = " "
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        if (nightMode)
            allThemeLogic()

        throughShortcut = intent.getBooleanExtra("throughShortcut", false)

        val typeface = Typeface.createFromAsset(assets, FontUtility.fontName)
        logo!!.typeface = typeface

        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)

        favourite_layout!!.setOnClickListener { startActivity(Intent(this@CollectionsActivity, Favorite::class.java)) }

        watchlist_layout!!.setOnClickListener { startActivity(Intent(this@CollectionsActivity, WatchList::class.java)) }


        client = GoogleApiClient.Builder(this).addApi(AppIndex.API).build()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            if (throughShortcut!!) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else
                finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupViewPager(viewPager: ViewPager?) {


        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(SavedMovies(), getString(R.string.offline))
        adapter.addFragment(Favorite(), getString(R.string.favorite))
        adapter.addFragment(WatchList(), getString(R.string.watchlist))
        viewPager!!.adapter = adapter

    }

    public override fun onStart() {
        super.onStart()

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client!!.connect()
        AppIndex.AppIndexApi.start(client, indexApiAction)
    }

    public override fun onStop() {
        super.onStop()

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, indexApiAction)
        client!!.disconnect()
    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onBackPressed() {
        if (throughShortcut!!) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else
            super.onBackPressed()
    }
}
