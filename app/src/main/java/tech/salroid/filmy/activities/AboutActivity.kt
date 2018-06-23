package tech.salroid.filmy.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.preference.PreferenceManager
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.utility.FontUtility


class AboutActivity : AppCompatActivity() {

    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null
    @BindView(R.id.logo)
    internal var logo: TextView? = null
    private var nightMode: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode)
            setTheme(R.style.AppTheme_Base_Dark)
        else
            setTheme(R.style.AppTheme_Base)

        setContentView(R.layout.activity_developers)


        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        if (supportActionBar != null) {

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = ""
        }


        val typeface = Typeface.createFromAsset(assets, FontUtility.fontName)
        logo!!.typeface = typeface



        if (nightMode)
            allThemeLogic()

    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


    fun sendEmail(view: View) {

        when (view.id) {


            R.id.email -> {

                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto: webianksc@gmailcom")
                startActivity(Intent.createChooser(emailIntent, "Send feedback"))
            }

            R.id.email2 -> {

                val emailIntent2 = Intent(Intent.ACTION_SENDTO)
                emailIntent2.data = Uri.parse("mailto: gupta.sajal631@gmail.com")
                startActivity(Intent.createChooser(emailIntent2, "Send feedback"))
            }
        }


    }


    fun openGithub(view: View) {
        val url = getString(R.string.webianksgit)
        val url2 = getString(R.string.salroidgit)

        when (view.id) {

            R.id.github -> {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            R.id.github2 -> {
                val builder1 = CustomTabsIntent.Builder()
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent1 = builder1.build()
                customTabsIntent1.launchUrl(this, Uri.parse(url2))
            }
        }

    }

    private fun viewIntent(url: String) {

        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun openGplus(view: View) {

        val url = getString(R.string.ramzplus)
        val url2 = getString(R.string.sajjuplus)

        when (view.id) {

            R.id.gplus -> {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent))
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
            R.id.gplus2 -> {
                val builder1 = CustomTabsIntent.Builder()
                builder1.setToolbarColor(ContextCompat.getColor(this, R.color.black))
                val customTabsIntent1 = builder1.build()
                customTabsIntent1.launchUrl(this, Uri.parse(url2))
            }
        }


    }


    override fun onResume() {
        super.onResume()

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)

        if (nightMode != nightModeNew)
            recreate()
    }
}
