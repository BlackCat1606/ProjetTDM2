package tech.salroid.filmy.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter
import tech.salroid.filmy.data_classes.CharacterDetailsData
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork



class FullMovieActivity : AppCompatActivity(), CharacterDetailsActivityAdapter.ClickListener {

    @BindView(R.id.toolbar) internal var toolbar: Toolbar? = null
    @BindView(R.id.full_movie_recycler)
    internal var full_movie_recycler: RecyclerView? = null
    private var movie_result: String? = null
    private var nightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_movie)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)



        full_movie_recycler!!.layoutManager = LinearLayoutManager(this@FullMovieActivity)


        val intent = intent
        if (intent != null) {
            movie_result = intent.getStringExtra("cast_json")
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = intent.getStringExtra("toolbar_title")
            }
        }


        val par = CharacterDetailActivityParseWork(this, movie_result!!)
        val char_list = par.char_parse_cast()
        val char_adapter = CharacterDetailsActivityAdapter(this, char_list, false)
        char_adapter.setClickListener(this)
        full_movie_recycler!!.adapter = char_adapter

    }


    override fun itemClicked(setterGetterChar: CharacterDetailsData, position: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", setterGetterChar.char_id)
        intent.putExtra("title", setterGetterChar.char_movie)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)

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