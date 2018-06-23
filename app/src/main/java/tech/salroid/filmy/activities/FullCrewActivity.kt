package tech.salroid.filmy.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.CastAdapter
import tech.salroid.filmy.custom_adapter.CrewAdapter
import tech.salroid.filmy.data_classes.CastDetailsData
import tech.salroid.filmy.data_classes.CrewDetailsData
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork


class FullCrewActivity : AppCompatActivity(), CrewAdapter.ClickListener {

    @BindView(R.id.toolbar) internal var toolbar: Toolbar? = null
    @BindView(R.id.full_cast_recycler) internal var full_crew_recycler: RecyclerView? = null


    private var crew_result: String? = null
    private var nightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_full_cast)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        full_crew_recycler!!.layoutManager = LinearLayoutManager(this@FullCrewActivity)


        val intent = intent
        if (intent != null) {
            crew_result = intent.getStringExtra("crew_json")
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = intent.getStringExtra("toolbar_title")
            }
        }


        val par = MovieDetailsActivityParseWork(this, crew_result!!)
        val crew_list = par.parse_crew()
        val full_crew_adapter = CrewAdapter(this, crew_list, false)
        full_crew_adapter.setClickListener(this)
        full_crew_recycler!!.adapter = full_crew_adapter

    }

    override fun itemClicked(setterGetter: CrewDetailsData, position: Int, view: View) {

        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.crew_id)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            val p1 = Pair.create(view.findViewById<View>(R.id.crew_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.crew_name), "name")

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2)
            startActivity(intent, options.toBundle())

        } else {
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew)
            recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if (item.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}