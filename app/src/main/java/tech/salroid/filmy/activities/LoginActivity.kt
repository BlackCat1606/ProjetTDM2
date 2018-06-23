package tech.salroid.filmy.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView

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
import tech.salroid.filmy.customs.CustomToast
import tech.salroid.filmy.network_stuff.TmdbVolleySingleton
import tech.salroid.filmy.utility.FontUtility



class LoginActivity : AppCompatActivity() {


    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.logo)
    internal var logo: TextView? = null
    internal var tmdbVolleySingleton = TmdbVolleySingleton.instance
    internal var tmdbrequestQueue = tmdbVolleySingleton!!.requestQueue
    private var progressDialog: ProgressDialog? = null
    private var tokenization = false
    private var requestToken: String? = null
    private var nightMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        setContentView(R.layout.activity_login)

        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setTitle(" ")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val typeface = Typeface.createFromAsset(assets, FontUtility.fontName)
        logo!!.typeface = typeface


        if (nightMode)
            allThemeLogic()

        login()

    }


    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }


    fun login() {


        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("TMDB Login")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.isIndeterminate = true
        progressDialog!!.show()

        logInBackground()


    }

    private fun logInBackground() {

        val api_key = BuildConfig.TMDB_API_KEY
        val BASE_URL = "https://api.themoviedb.org/3/authentication/token/new?api_key=" + api_key

        val jsonObjectRequest = JsonObjectRequest(BASE_URL, null,
                Response.Listener { response -> parseOutput(response) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })
        tmdbrequestQueue.add(jsonObjectRequest)


    }

    private fun parseOutput(response: JSONObject) {

        try {

            val status = response.getBoolean("success")

            if (status) {

                requestToken = response.getString("request_token")
                tokenization = true
                validateToken(requestToken)


            } else {

                progressDialog!!.dismiss()
                CustomToast.show(this, "Failed to login", false)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun validateToken(requestToken: String?) {

        val url = "https://www.themoviedb.org/authenticate/" + requestToken!!
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))

    }


    override fun onResume() {
        super.onResume()

        if (tokenization && requestToken != null) {

            val api_key = BuildConfig.TMDB_API_KEY
            val SESSION_QUERY = "https://api.themoviedb.org/3/authentication/session/new?api_key=$api_key&request_token=$requestToken"
            querySession(SESSION_QUERY)
        }

    }


    private fun querySession(session_query: String) {

        val jsonObjectRequest = JsonObjectRequest(session_query, null,
                Response.Listener { response -> parseSession(response) }, Response.ErrorListener { error -> Log.e("webi", "Volley Error: " + error.cause) })

        tmdbrequestQueue.add(jsonObjectRequest)


    }

    private fun parseSession(response: JSONObject) {

        try {

            val status = response.getBoolean("success")

            if (status) {

                val session_id = response.getString("session_id")
                progressDialog!!.dismiss()

                val resultIntent = Intent()
                resultIntent.putExtra("session_id", session_id)
                setResult(Activity.RESULT_OK, resultIntent)

                finish()

            } else {

                progressDialog!!.dismiss()
                CustomToast.show(this, "Failed to login", false)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun login(view: View) {

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("TMDB Login")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.isIndeterminate = true
        progressDialog!!.show()

        logInBackground()
    }
}
