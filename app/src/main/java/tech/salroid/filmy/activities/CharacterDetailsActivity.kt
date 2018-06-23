package tech.salroid.filmy.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mikhaellopez.circularimageview.CircularImageView

import org.json.JSONException
import org.json.JSONObject

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter
import tech.salroid.filmy.data_classes.CharacterDetailsData
import tech.salroid.filmy.fragment.FullReadFragment
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork
import tech.salroid.filmy.utility.FontUtility



class CharacterDetailsActivity : AppCompatActivity(), CharacterDetailsActivityAdapter.ClickListener {


    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.more)
    internal var more: TextView? = null
    @BindView(R.id.cast_name)
    internal var ch_name: TextView? = null
    @BindView(R.id.desc)
    internal var ch_desc: TextView? = null
    @BindView(R.id.birth)
    internal var ch_birth: TextView? = null
    @BindView(R.id.birth_place)
    internal var ch_place: TextView? = null
    @BindView(R.id.character_movies)
    internal var char_recycler: RecyclerView? = null
    @BindView(R.id.header_container)
    internal var headerContainer: FrameLayout? = null
    @BindView(R.id.cast_poster)
    internal var character_small: CircularImageView? = null
    @BindView(R.id.logo)
    internal var logo: TextView? = null


    internal var co: Context = this
    private var character_id: String? = null
    private var character_title: String? = null
    private var movie_json: String? = null
    private var character_bio: String? = null
    private var fullReadFragment: FullReadFragment? = null

    private var nightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_cast)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)


        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""


        val typeface = Typeface.createFromAsset(assets, FontUtility.fontName)
        logo!!.typeface = typeface


        if (nightMode)
            allThemeLogic()

        more!!.setOnClickListener {
            if (!(movie_json == null && character_title == null)) {
                val intent = Intent(this@CharacterDetailsActivity, FullMovieActivity::class.java)
                intent.putExtra("cast_json", movie_json)
                intent.putExtra("toolbar_title", character_title)
                startActivity(intent)

            }
        }


        char_recycler!!.layoutManager = LinearLayoutManager(this@CharacterDetailsActivity)
        char_recycler!!.isNestedScrollingEnabled = false

        headerContainer!!.setOnClickListener {
            if (character_title != null && character_bio != null) {

                fullReadFragment = FullReadFragment()
                val args = Bundle()
                args.putString("title", character_title)
                args.putString("desc", character_bio)

                fullReadFragment!!.arguments = args

                supportFragmentManager.beginTransaction()
                        .replace(R.id.main, fullReadFragment).addToBackStack("DESC").commit()
            }
        }


        val intent = intent
        if (intent != null) {
            character_id = intent.getStringExtra("id")
        }


        getDetailedMovieAndCast()

    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onResume() {
        super.onResume()

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew)
            recreate()
    }

    private fun getDetailedMovieAndCast() {


        val volleySingleton = TmdbVolleySingleton.instance
        val requestQueue = volleySingleton!!.requestQueue

        val api_key = BuildConfig.TMDB_API_KEY

        val BASE_URL_PERSON_DETAIL = "https://api.themoviedb.org/3/person/$character_id?api_key=$api_key"

        val BASE_URL_PEOPLE_MOVIES = "https://api.themoviedb.org/3/person/$character_id/movie_credits?api_key=$api_key"

        val personDetailRequest = JsonObjectRequest(BASE_URL_PERSON_DETAIL, null,
                Response.Listener { response -> personDetailsParsing(response.toString()) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) }
        )

        val personMovieDetailRequest = JsonObjectRequest(BASE_URL_PEOPLE_MOVIES, null,
                Response.Listener { response ->
                    movie_json = response.toString()

                    cast_parseOutput(response.toString())
                }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) }
        )


        requestQueue.add(personDetailRequest)
        requestQueue.add(personMovieDetailRequest)


    }


    override fun itemClicked(setterGetterchar: CharacterDetailsData, position: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", setterGetterchar.char_id)
        intent.putExtra("title", setterGetterchar.char_movie)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)
    }


    internal fun personDetailsParsing(detailsResult: String) {


        try {
            val jsonObject = JSONObject(detailsResult)

            val char_name = jsonObject.getString("name")
            val char_face = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("profile_path")
            val char_desc = jsonObject.getString("biography")
            val char_birthday = jsonObject.getString("birthday")
            val char_birthplace = jsonObject.getString("place_of_birth")

            character_title = char_name
            character_bio = char_desc

            ch_name!!.text = char_name
            if (char_birthday == "null")
                ch_birth!!.visibility = View.GONE
            else
                ch_birth!!.text = char_birthday
            if (char_birthplace == "null")
                ch_place!!.visibility = View.GONE
            else
                ch_place!!.text = char_birthplace
            if (char_desc.length <= 0) {
                headerContainer!!.visibility = View.GONE
                ch_desc!!.visibility = View.GONE
            } else {
                if (Build.VERSION.SDK_INT >= 24) {
                    ch_desc!!.text = Html.fromHtml(char_desc, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    ch_desc!!.text = Html.fromHtml(char_desc)
                }
            }

            /*Glide.with(co)
                  .load(char_banner)
                    .fitCenter()
                  .into(character_banner);*/


            try {

                Glide.with(co)
                        .load(char_face)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().into(character_small!!)

            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun cast_parseOutput(cast_result: String) {

        val par = CharacterDetailActivityParseWork(this, cast_result)
        val char_list = par.char_parse_cast()
        val char_adapter = CharacterDetailsActivityAdapter(this, char_list, true)
        char_adapter.setClickListener(this)
        char_recycler!!.adapter = char_adapter
        if (char_list.size > 4)
            more!!.visibility = View.VISIBLE
        else
            more!!.visibility = View.INVISIBLE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //reverse transition
                supportFinishAfterTransition()
            } else
                finish()

        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {

        val fragment = supportFragmentManager.findFragmentByTag("DESC") as FullReadFragment
        if (fragment != null && fragment.isVisible) {
            supportFragmentManager.beginTransaction().remove(fullReadFragment).commit()
        } else {
            super.onBackPressed()
        }
    }


    override fun onStop() {
        super.onStop()
        Glide.clear(character_small!!)
    }
}