package tech.salroid.filmy.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.youtube.player.YouTubeStandalonePlayer

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.Arrays
import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.animations.RevealAnimation
import tech.salroid.filmy.customs.BreathingProgress
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.database.MovieDetailsUpdation
import tech.salroid.filmy.database.MovieLoaders
import tech.salroid.filmy.database.MovieProjection
import tech.salroid.filmy.database.OfflineMovies
import tech.salroid.filmy.fragment.AllTrailerFragment
import tech.salroid.filmy.fragment.CastFragment
import tech.salroid.filmy.fragment.CrewFragment
import tech.salroid.filmy.fragment.FullReadFragment
import tech.salroid.filmy.fragment.SimilarFragment
import tech.salroid.filmy.network_stuff.GetDataFromNetwork
import tech.salroid.filmy.utility.Confirmation
import tech.salroid.filmy.utility.Constants
import tech.salroid.filmy.utility.NullChecker



class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, GetDataFromNetwork.DataFetchedListener, CastFragment.GotCrewListener {


    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.detail_title)
    internal var det_title: TextView? = null
    @BindView(R.id.detail_tagline)
    internal var det_tagline: TextView? = null
    @BindView(R.id.detail_overview)
    internal var det_overview: TextView? = null
    @BindView(R.id.tmdbRating)
    internal var det_rating: TextView? = null
    @BindView(R.id.tomatoRating)
    internal var tomato_rating: TextView? = null
    @BindView(R.id.flixterRating)
    internal var flixter_rating: TextView? = null
    @BindView(R.id.metaRating)
    internal var meta_rating: TextView? = null
    @BindView(R.id.imdbRating)
    internal var rating_of_imdb: TextView? = null
    @BindView(R.id.metaRatingView)
    internal var metascore_setter: TextView? = null
    @BindView(R.id.detail_released)
    internal var det_released: TextView? = null
    @BindView(R.id.detail_certification)
    internal var det_certification: TextView? = null
    @BindView(R.id.detail_runtime)
    internal var det_runtime: TextView? = null
    @BindView(R.id.detail_language)
    internal var det_language: TextView? = null
    @BindView(R.id.detail_youtube)
    internal var youtube_link: ImageView? = null
    @BindView(R.id.backdrop)
    internal var banner: ImageView? = null
    @BindView(R.id.play_button)
    internal var youtube_play_button: ImageView? = null
    @BindView(R.id.tomatoRating_image)
    internal var tomatoRating_image: ImageView? = null
    @BindView(R.id.flixterRating_image)
    internal var flixterRating_image: ImageView? = null
    @BindView(R.id.breathingProgress)
    internal var breathingProgress: BreathingProgress? = null
    @BindView(R.id.trailorBackground)
    internal var trailorBackground: LinearLayout? = null

    @BindView(R.id.youtube_icon)
    internal var youtubeIcon: ImageView? = null

    @BindView(R.id.trailorView)
    internal var trailorView: FrameLayout? = null
    @BindView(R.id.new_main)
    internal var newMain: FrameLayout? = null
    @BindView(R.id.all_details_container)
    internal var main_content: FrameLayout? = null
    @BindView(R.id.header_container)
    internal var headerContainer: FrameLayout? = null
    @BindView(R.id.main)
    internal var main: RelativeLayout? = null
    @BindView(R.id.metaRating_background)
    internal var metaRating_background: RelativeLayout? = null
    @BindView(R.id.header)
    internal var header: LinearLayout? = null

    @BindView(R.id.extraDetails)
    internal var extraDetails: RelativeLayout? = null

    @BindView(R.id.ratingBar)
    internal var ratingBar: RelativeLayout? = null

    @BindView(R.id.cast_divider)
    internal var castDivider: View? = null

    @BindView(R.id.layout_imdb)
    internal var layout_imdb: LinearLayout? = null

    @BindView(R.id.layout_flixi)
    internal var layout_flixi: LinearLayout? = null

    @BindView(R.id.layout_meta)
    internal var layout_meta: LinearLayout? = null

    @BindView(R.id.layout_tmdb)
    internal var layout_tmdb: LinearLayout? = null

    @BindView(R.id.layout_tomato)
    internal var layout_tomato: LinearLayout? = null


    internal var context: Context = this
    internal lateinit var trailer_array: Array<String?>
    internal lateinit var trailer_array_name: Array<String?>
    internal lateinit var fullReadFragment: FullReadFragment
    internal lateinit var allTrailerFragment: AllTrailerFragment
    internal lateinit var movieMap: HashMap<String, String>
    internal var networkApplicable: Boolean = false
    internal var databaseApplicable: Boolean = false
    internal var savedDatabaseApplicable: Boolean = false
    internal var trailer_boolean = false
    internal var type: Int = 0
    private var movie_id: String? = null
    private var trailor: String? = null
    private var trailer: String? = null
    private var movie_desc: String? = null
    private var quality: String? = null
    private var movie_tagline: String? = null
    private val movie_rating: String? = null
    private var movie_rating_tmdb: String? = null
    private var show_centre_img_url: String? = null
    private var movie_title: String? = null
    private var movie_id_final: String? = null

    private var castFragment: CastFragment? = null
    private var nightMode: Boolean = false
    private var movie_imdb_id: String? = null
    private var crewFragment: CrewFragment? = null
    private var similarFragment: SimilarFragment? = null
    private var movie_rating_audience: String? = null
    private var movie_rating_metascore: String? = null
    private var movie_title_hyphen: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.DetailsActivityThemeDark)
        else
            setTheme(R.style.DetailsActivityTheme)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        ButterKnife.bind(this)

        if (!nightMode)
            allThemeLogic()
        else {
            nightModeLogic()
            castDivider!!.visibility = View.GONE
        }

        setSupportActionBar(toolbar)

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val prefrence = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailsActivity)
        quality = prefrence.getString("image_quality", "w1000")

        headerContainer!!.setOnClickListener(this)
        newMain!!.setOnClickListener(this)
        trailorView!!.setOnClickListener(this)
        youtubeIcon!!.setOnClickListener(this)


        val intent = intent
        getDataFromIntent(intent)

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(main_content)
            performDataFetching()
        }
        showCastFragment()
        showCrewFragment()
        showSimilarFragment()

    }

    private fun nightModeLogic() {

        main_content!!.setBackgroundColor(Color.parseColor("#212121"))
        headerContainer!!.setBackgroundColor(Color.parseColor("#212121"))
        extraDetails!!.setBackgroundColor(Color.parseColor("#212121"))
        ratingBar!!.setBackgroundColor(Color.parseColor("#212121"))

    }

    private fun allThemeLogic() {

        main_content!!.setBackgroundColor(Color.parseColor("#f5f5f5"))
        headerContainer!!.setBackgroundColor(resources.getColor(R.color.primaryColor))
        extraDetails!!.setBackgroundColor(resources.getColor(R.color.primaryColor))
        ratingBar!!.setBackgroundColor(resources.getColor(R.color.primaryColor))
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew)
            recreate()

        performDataFetching()

        /* showCastFragment(); //TODO :: salroid finds it un-necessary
        showCrewFragment();
        showSimilarFragment();*/
    }

    private fun performDataFetching() {

        val getStuffFromNetwork = GetDataFromNetwork()
        getStuffFromNetwork.setDataFetchedListener(this)

        if (networkApplicable)
            getStuffFromNetwork.getMovieDetailsFromNetwork(movie_id!!)

        if (databaseApplicable)
            supportLoaderManager.initLoader(MovieLoaders.MOVIE_DETAILS_LOADER, null, this)

        if (savedDatabaseApplicable)
            supportLoaderManager.initLoader(MovieLoaders.SAVED_MOVIE_DETAILS_LOADER, null, this)

        if (!databaseApplicable && !savedDatabaseApplicable) {

            main!!.visibility = View.INVISIBLE
            breathingProgress!!.setVisibility(View.VISIBLE)
        }
    }


    private fun getDataFromIntent(intent: Intent?) {

        if (intent != null) {

            networkApplicable = intent.getBooleanExtra("network_applicable", false)
            databaseApplicable = intent.getBooleanExtra("database_applicable", false)
            savedDatabaseApplicable = intent.getBooleanExtra("saved_database_applicable", false)
            type = intent.getIntExtra("type", 0)
            movie_id = intent.getStringExtra("id")
            movie_title = intent.getStringExtra("title")
        }
    }

    private fun showCastFragment() {

        castFragment = CastFragment.newInstance(null.toString(), movie_title!!)

        supportFragmentManager.beginTransaction().replace(R.id.cast_container, castFragment)
                .commit()

        castFragment!!.setGotCrewListener(this)
    }


    private fun showCrewFragment() {

        crewFragment = CrewFragment.newInstance(null.toString(), movie_title!!)

        supportFragmentManager.beginTransaction().replace(R.id.crew_container, crewFragment)
                .commit()
    }

    private fun showSimilarFragment() {
        similarFragment = SimilarFragment.newInstance(null.toString(), movie_title!!)

        supportFragmentManager.beginTransaction().replace(R.id.similar_container, similarFragment)
                .commit()
    }

    internal fun parseMovieDetails(movieDetails: String) {

        val title: String
        val tagline: String
        val overview: String
        val banner_profile: String
        val runtime: String
        val language: String
        val released: String
        val poster: String
        var img_url: String? = null
        val get_poster_path_from_json: String
        val get_banner_from_json: String

        try {

            val jsonObject = JSONObject(movieDetails)
            title = jsonObject.getString("title")
            tagline = jsonObject.getString("tagline")
            overview = jsonObject.getString("overview")
            released = jsonObject.getString("release_date")
            runtime = jsonObject.getString("runtime") + " mins"
            language = jsonObject.getString("original_language")

            movie_id_final = jsonObject.getString("id")
            movie_imdb_id = jsonObject.getString("imdb_id")
            movie_title_hyphen = movie_title!!.replace(' ', '-')
            movie_rating_tmdb = jsonObject.getString("vote_average")

            if (tagline != "")
                det_tagline!!.visibility = View.VISIBLE

            if (castFragment != null)
                castFragment!!.getCastFromNetwork(movie_id_final!!)

            if (similarFragment != null)
                similarFragment!!.getSimilarFromNetwork(movie_id_final!!)

            Rating.getRating(context, movie_imdb_id!!)

            //poster and banner
            get_poster_path_from_json = jsonObject.getString("poster_path")
            get_banner_from_json = jsonObject.getString("backdrop_path")

            poster = resources.getString(R.string.poster_prefix_185) + get_poster_path_from_json

            val banner_for_full_activity: String
            val poster_prefix_500 = resources.getString(R.string.poster_prefix_500)
            val poster_prefix_add_quality = resources.getString(R.string.poster_prefix_add_quality)

            if (get_banner_from_json != "null") {
                banner_profile = poster_prefix_500 + get_banner_from_json
                banner_for_full_activity = poster_prefix_add_quality + quality + get_banner_from_json

            } else {
                banner_profile = poster_prefix_500 + get_poster_path_from_json
                banner_for_full_activity = poster_prefix_add_quality + quality + get_poster_path_from_json
            }

            //trailer
            val trailorsObject = jsonObject.getJSONObject("trailers")
            val youTubeArray = trailorsObject.getJSONArray("youtube")

            trailer_array = arrayOfNulls(youTubeArray.length())
            trailer_array_name = arrayOfNulls(youTubeArray.length())

            if (youTubeArray.length() != 0) {
                var main_trailer: Boolean? = true
                for (i in 0 until youTubeArray.length()) {

                    val singleTrailor = youTubeArray.getJSONObject(i)
                    trailer_array[i] = singleTrailor.getString("source")
                    trailer_array_name[i] = singleTrailor.getString("name")


                    val type = singleTrailor.getString("type")
                    if (main_trailer!!) {
                        if (type == "Trailer") {
                            trailor = singleTrailor.getString("source")
                            main_trailer = false
                        } else
                            trailor = youTubeArray.getJSONObject(0).getString("source")
                    }
                }
                trailer = resources.getString(R.string.trailer_link_prefix) + trailor!!
            } else
                trailer = null
            //genre
            var genre = ""
            val genreArray = jsonObject.getJSONArray("genres")
            for (i in 0 until genreArray.length()) {
                if (i > 3)
                    break
                val finalgenre = genreArray.getJSONObject(i).getString("name")
                var punctuation = ", "
                if (i == genre.length)
                    punctuation = ""
                genre = genre + punctuation + finalgenre

            }

            movie_desc = overview
            movie_title = title
            movie_tagline = tagline
            show_centre_img_url = banner_for_full_activity

            movieMap = HashMap()
            movieMap.put("imdb_id", movie_id_final!!)
            movieMap.put("title", movie_title!!)
            movieMap.put("tagline", tagline)
            movieMap.put("overview", overview)
            movieMap.put("rating", movie_rating!!)
            movieMap.put("certification", genre)
            movieMap.put("language", language)
            movieMap.put("released", released)
            movieMap.put("runtime", runtime)
            movieMap.put("trailer", trailer!!)
            movieMap.put("banner", banner_profile)
            movieMap.put("poster", poster)

            try {
                if (trailor != null) {
                    trailer_boolean = true
                    //  String videoId = extractYoutubeId(trailer);
                    img_url = (resources.getString(R.string.trailer_img_prefix) + trailor
                            + resources.getString(R.string.trailer_img_suffix))

                } else {
                    img_url = resources.getString(R.string.poster_prefix_185) + jsonObject.getString("poster_path")

                }
                movieMap.put("trailer_img", img_url)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {

                if (databaseApplicable)
                    MovieDetailsUpdation.performMovieDetailsUpdation(this@MovieDetailsActivity, type, movieMap, movie_id!!)
                else
                    showParsedContent(title, banner_profile, img_url, tagline, overview, movie_rating, runtime, released, genre, language)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun showParsedContent(title: String, banner_profile: String, img_url: String?, tagline: String,
                                  overview: String, rating: String?, runtime: String,
                                  released: String, certification: String, language: String) {

        det_tagline!!.text = tagline
        det_title!!.text = title
        det_overview!!.text = overview
        //det_rating.setText(rating);
        det_runtime!!.text = runtime
        det_released!!.text = released
        det_certification!!.text = certification
        det_language!!.text = language


        try {
            Glide.with(context)
                    .load(banner_profile)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {

                            banner!!.setImageBitmap(resource)
                            Palette.from(resource).generate { p ->
                                // Use generated instance
                                val swatch = p.vibrantSwatch
                                val trailorSwatch = p.darkVibrantSwatch

                                if (swatch != null) {
                                    header!!.setBackgroundColor(swatch.rgb)
                                    det_title!!.setTextColor(swatch.titleTextColor)
                                    det_tagline!!.setTextColor(swatch.bodyTextColor)
                                    det_overview!!.setTextColor(swatch.bodyTextColor)
                                }
                                if (trailorSwatch != null) {
                                    trailorBackground!!.setBackgroundColor(trailorSwatch.rgb)
                                    youtubeIcon!!.setColorFilter(trailorSwatch.bodyTextColor, PorterDuff.Mode.SRC_IN)
                                }
                            }
                        }
                    })

        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

        try {

            Glide.with(context)
                    .load(img_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            youtube_link!!.setImageBitmap(resource)
                            if (trailer_boolean)
                                youtube_play_button!!.visibility = View.VISIBLE
                        }

                    })
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

        main!!.visibility = View.VISIBLE
        breathingProgress!!.setVisibility(View.INVISIBLE)

    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor>? {

        var cursorloader: CursorLoader? = null

        if (id == MovieLoaders.MOVIE_DETAILS_LOADER) {

            when (type) {

                0 -> cursorloader = CursorLoader(this,
                        FilmContract.MoviesEntry.buildMovieWithMovieId(movie_id!!),
                        MovieProjection.GET_MOVIE_COLUMNS, null, null, null)

                1 -> cursorloader = CursorLoader(this,
                        FilmContract.InTheatersMoviesEntry.buildMovieWithMovieId(movie_id!!),
                        MovieProjection.GET_MOVIE_COLUMNS, null, null, null)

                2 -> cursorloader = CursorLoader(this,
                        FilmContract.UpComingMoviesEntry.buildMovieWithMovieId(movie_id!!),
                        MovieProjection.GET_MOVIE_COLUMNS, null, null, null)
            }

        } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {

            val selection = FilmContract.SaveEntry.TABLE_NAME +
                    "." + FilmContract.SaveEntry.SAVE_ID + " = ? "
            val selectionArgs = arrayOf<String>(movie_id!!)

            cursorloader = CursorLoader(this, FilmContract.SaveEntry.CONTENT_URI,
                    MovieProjection.GET_SAVE_COLUMNS, selection, selectionArgs, null)

        }
        return cursorloader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {

        val id = loader.id

        if (id == MovieLoaders.MOVIE_DETAILS_LOADER) {

            fetchMovieDetailsFromCursor(data)

        } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {

            fetchSavedMovieDetailsFromCursor(data)
        }

    }

    private fun fetchSavedMovieDetailsFromCursor(data: Cursor?) {

        if (data != null && data.moveToFirst()) {

            val title_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE)
            val banner_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_BANNER)
            val tagline_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TAGLINE)
            val description_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_DESCRIPTION)
            val trailer_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TRAILER)
            val rating_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING)
            val released_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING)
            val runtime_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RUNTIME)
            val language_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_LANGUAGE)
            val certification_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_CERTIFICATION)
            val id_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)
            val poster_link_index = data.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK)

            val title = data.getString(title_index)
            val banner_url = data.getString(banner_index)
            val tagline = data.getString(tagline_index)
            val overview = data.getString(description_index)

            //as it will be used to show it on YouTube
            trailer = data.getString(trailer_index)
            val posterLink = data.getString(poster_link_index)

            val rating = data.getString(rating_index)
            val runtime = data.getString(runtime_index)
            val released = data.getString(released_index)
            val certification = data.getString(certification_index)
            val language = data.getString(language_index)

            movie_id_final = data.getString(id_index)

            det_tagline!!.text = tagline
            det_title!!.text = title
            det_overview!!.text = overview
            //det_rating.setText(rating);
            det_runtime!!.text = runtime
            det_released!!.text = released
            det_certification!!.text = certification
            det_language!!.text = language

            movie_desc = overview
            show_centre_img_url = banner_url


            try {

                Glide.with(context)
                        .load(banner_url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {

                                banner!!.setImageBitmap(resource)
                                Palette.from(resource).generate { p ->
                                    // Use generated instance
                                    val swatch = p.vibrantSwatch
                                    val trailorSwatch = p.darkVibrantSwatch

                                    if (swatch != null) {
                                        header!!.setBackgroundColor(swatch.rgb)
                                        det_title!!.setTextColor(swatch.titleTextColor)
                                        det_tagline!!.setTextColor(swatch.bodyTextColor)
                                        det_overview!!.setTextColor(swatch.bodyTextColor)
                                    }
                                    if (trailorSwatch != null) {
                                        trailorBackground!!.setBackgroundColor(trailorSwatch.rgb)
                                        youtubeIcon!!.setColorFilter(trailorSwatch.bodyTextColor, PorterDuff.Mode.SRC_IN)
                                    }
                                }

                            }
                        })
            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }

            var thumbNail: String? = null
            if (trailor != null) {
                trailer_boolean = true
                thumbNail = (resources.getString(R.string.trailer_img_prefix) + trailor
                        + resources.getString(R.string.trailer_img_prefix))
            } else {
                thumbNail = posterLink
            }

            try {

                Glide.with(context)
                        .load(thumbNail)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                                youtube_link!!.setImageBitmap(resource)
                                if (trailer_boolean)
                                    youtube_play_button!!.visibility = View.VISIBLE
                            }
                        })

            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }

        }
    }

    private fun fetchMovieDetailsFromCursor(data: Cursor?) {

        if (data != null && data.moveToFirst()) {

            val title_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
            val banner_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_BANNER)
            val tagline_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TAGLINE)
            val description_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_DESCRIPTION)
            val trailer_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TRAILER)
            val rating_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RATING)
            val released_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RELEASED)
            val runtime_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RUNTIME)
            val language_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_LANGUAGE)
            val certification_index = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_CERTIFICATION)

            val title = data.getString(title_index)
            val banner_url = data.getString(banner_index)
            val tagline = data.getString(tagline_index)
            val overview = data.getString(description_index)
            val trailer = data.getString(trailer_index)
            val rating = data.getString(rating_index)
            val runtime = data.getString(runtime_index)
            val released = data.getString(released_index)
            val certification = data.getString(certification_index)
            val language = data.getString(language_index)


            if (NullChecker.isSettable(title))
                det_title!!.text = title

            if (NullChecker.isSettable(tagline))
                det_tagline!!.text = tagline

            if (NullChecker.isSettable(overview))
                det_overview!!.text = overview

            if (runtime != null && runtime != "null mins")
                det_runtime!!.text = runtime

            if (NullChecker.isSettable(released))
                det_released!!.text = released

            if (NullChecker.isSettable(certification))
                det_certification!!.text = certification

            if (NullChecker.isSettable(language))
                det_language!!.text = language

            try {
                Glide.with(context)
                        .load(banner_url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {

                                banner!!.setImageBitmap(resource)

                                Palette.from(resource).generate { p ->
                                    val swatch = p.vibrantSwatch
                                    val trailorSwatch = p.darkVibrantSwatch

                                    if (swatch != null) {

                                        header!!.setBackgroundColor(swatch.rgb)
                                        det_title!!.setTextColor(swatch.titleTextColor)
                                        det_tagline!!.setTextColor(swatch.bodyTextColor)
                                        det_overview!!.setTextColor(swatch.bodyTextColor)
                                    }
                                    if (trailorSwatch != null) {
                                        trailorBackground!!.setBackgroundColor(trailorSwatch.rgb)
                                        youtubeIcon!!.setColorFilter(trailorSwatch.bodyTextColor, PorterDuff.Mode.SRC_IN)
                                    }
                                }

                            }
                        })
            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }

            try {

                Glide.with(context)
                        .load(trailer)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                                youtube_link!!.setImageBitmap(resource)
                                if (trailer_boolean)
                                    youtube_play_button!!.visibility = View.VISIBLE
                            }

                        })
            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }

        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                finish()

                if (type == -1)
                    startActivity(Intent(this, MainActivity::class.java))
            }

            R.id.action_share -> shareMovie()

            R.id.action_save -> {
                val offlineMovies = OfflineMovies(this)
                offlineMovies.saveMovie(movieMap, movie_id!!, movie_id_final!!, Constants.FLAG_OFFLINE)
            }

            R.id.action_fav ->

                Confirmation.confirmFav(this, movieMap, movie_id!!, movie_id_final!!, Constants.FLAG_FAVORITE)

            R.id.action_watch ->

                Confirmation.confirmWatchlist(this, movieMap, movie_id!!, movie_id_final!!, Constants.FLAG_WATCHLIST)

            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if (fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.movie_detail_menu, menu)
        menu.findItem(R.id.action_save).isVisible = !savedDatabaseApplicable
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.header_container -> if (movie_title != null && movie_desc != null) {
                fullReadFragment = FullReadFragment()
                val args = Bundle()
                args.putString("title", movie_title)
                args.putString("desc", movie_desc)
                fullReadFragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.all_details_container, fullReadFragment).addToBackStack("DESC").commit()
            }

            R.id.new_main -> if (show_centre_img_url != null) {
                val intent = Intent(this@MovieDetailsActivity, FullScreenImage::class.java)
                intent.putExtra("img_url", show_centre_img_url)
                startActivity(intent)
            }

            R.id.trailorView -> if (trailer_boolean)
                startActivity(YouTubeStandalonePlayer.createVideoIntent(this@MovieDetailsActivity,
                        getString(R.string.Youtube_Api_Key), trailor!!))
            R.id.youtube_icon -> if (trailer_boolean) {
                allTrailerFragment = AllTrailerFragment()
                // Log.d(TAG, "onClick: "+ Arrays.toString(trailer_array));
                val args = Bundle()
                args.putString("title", movie_title)
                args.putStringArray("trailers", trailer_array)
                args.putStringArray("trailers_name", trailer_array_name)
                allTrailerFragment.arguments = args
                supportFragmentManager.beginTransaction()
                        .replace(R.id.all_details_container, allTrailerFragment).addToBackStack("TRAILER").commit()
            }
        }

    }

    override fun dataFetched(response: String, code: Int) {
        when (code) {

            GetDataFromNetwork.MOVIE_DETAILS_CODE ->


                parseMovieDetails(response)

            GetDataFromNetwork.CAST_CODE -> {
            }
        }// showCastFragment();%
    }

    private fun shareMovie() {
        val movie_imdb = resources.getString(R.string.imdb_link_prefix) + movie_imdb_id!!
        if (!(movie_title == null && movie_rating == "null" && movie_imdb_id == "null")) {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(Intent.EXTRA_TEXT, "*$movie_title*\n$movie_tagline\n$movie_imdb\n")
            startActivity(Intent.createChooser(myIntent, "Share with"))
        }
    }

    override fun onStop() {
        super.onStop()
        castFragment = null
        similarFragment = null

    }

    override fun gotCrew(crewData: String) {

        if (crewFragment != null)
            crewFragment!!.crew_parseOutput(crewData)
    }

    fun setRating(movie_rating_imdb: String, movie_rating_tomatometer: String,
                  audience_rating: String, metascore_rating: String, rottenTomatoPage: String) {

        movie_rating_audience = audience_rating
        movie_rating_metascore = metascore_rating

        if (movie_rating_imdb == "N/A")
            layout_imdb!!.visibility = View.GONE
        else {
            rating_of_imdb!!.text = movie_rating_imdb
            layout_imdb!!.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, R.color.imdbYellow))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MovieDetailsActivity, Uri.parse(resources.getString(R.string.imdb_link_prefix) + movie_imdb_id!!))
            }
        }

        if (movie_rating_tomatometer == "N/A")
            layout_tomato!!.visibility = View.GONE
        else {

            /* if (image.equals("certified"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.certified));
            else if (image.equals("fresh"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.fresh));
            else if (image.equals("rotten"))
                tomatoRating_image.setImageDrawable(getResources().getDrawable(R.drawable.rotten));*/

            // Image Logic Changed According to %age due to OMDB API limitations

            val tomatometer_score = Integer.parseInt(movie_rating_tomatometer.substring(0, movie_rating_tomatometer.length - 1))
            if (tomatometer_score > 74)
                tomatoRating_image!!.setImageDrawable(resources.getDrawable(R.drawable.certified))
            else if (tomatometer_score > 59)
                tomatoRating_image!!.setImageDrawable(resources.getDrawable(R.drawable.fresh))
            else if (tomatometer_score < 60)
                tomatoRating_image!!.setImageDrawable(resources.getDrawable(R.drawable.rotten))

            tomato_rating!!.text = movie_rating_tomatometer
            layout_tomato!!.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, R.color.tomatoRed))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MovieDetailsActivity, Uri.parse(rottenTomatoPage))
            }
        }

        if (movie_rating_audience == "N/A")
            layout_flixi!!.visibility = View.GONE
        else {

            val audi_rating = java.lang.Float.valueOf(audience_rating)!!

            if (audi_rating > 3.4)
                flixterRating_image!!.setImageDrawable(resources.getDrawable(R.drawable.popcorn))
            else
                flixterRating_image!!.setImageDrawable(resources.getDrawable(R.drawable.spilt))

            flixter_rating!!.text = movie_rating_audience
        }

        if (movie_rating_metascore == "N/A")
            layout_meta!!.visibility = View.GONE
        else {
            var smallTitle = movie_title_hyphen!!.toLowerCase()
            smallTitle = smallTitle.replace("[^0-9-a-z]".toRegex(), "")
            val url = "http://www.metacritic.com/movie/" + smallTitle
            val metasco_rating = Integer.valueOf(metascore_rating)!!
            if (metasco_rating > 60)
                metaRating_background!!.setBackgroundColor(Color.parseColor("#66cc33"))
            else if (metasco_rating > 40 && metasco_rating < 61)
                metaRating_background!!.setBackgroundColor(Color.parseColor("#ffcc33"))
            else
                metaRating_background!!.setBackgroundColor(Color.parseColor("#ff0000"))


            meta_rating!!.text = movie_rating_metascore
            metascore_setter!!.text = movie_rating_metascore
            layout_meta!!.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, R.color.metaBlack))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MovieDetailsActivity, Uri.parse(url))
            }
        }

        if (movie_rating_tmdb == "0")
            layout_tmdb!!.visibility = View.GONE
        else {
            det_rating!!.text = movie_rating_tmdb
            layout_tmdb!!.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, R.color.tmdbGreen))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MovieDetailsActivity, Uri.parse("https://www.themoviedb.org/movie/$movie_id-$movie_title_hyphen"))
            }
        }
    }

    fun setRatingGone() {
        ratingBar!!.visibility = View.GONE
    }
}