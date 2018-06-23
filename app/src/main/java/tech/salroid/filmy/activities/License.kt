package tech.salroid.filmy.activities

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.MenuItem
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.utility.FontUtility



class License : AppCompatActivity() {

    @BindView(R.id.glide)
    internal var glide: TextView? = null
    @BindView(R.id.materialsearcview)
    internal var material: TextView? = null
    @BindView(R.id.circularimageview)
    internal var circularimageview: TextView? = null
    @BindView(R.id.tatarka)
    internal var tatarka: TextView? = null
    @BindView(R.id.error)
    internal var error: TextView? = null
    @BindView(R.id.appintro)
    internal var appintro: TextView? = null
    @BindView(R.id.butterknife)
    internal var butterknife: TextView? = null
    @BindView(R.id.crashlytics)
    internal var crashlytics: TextView? = null
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

        setContentView(R.layout.activity_license)

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

        if (Build.VERSION.SDK_INT >= 24)
            v24Setup()
        else
            normalSetup()
    }

    private fun normalSetup() {

        glide!!.text = Html.fromHtml(getString(R.string.glide))
        material!!.text = Html.fromHtml(getString(R.string.materialsearch))
        circularimageview!!.text = Html.fromHtml(getString(R.string.circularimageview))
        tatarka!!.text = Html.fromHtml(getString(R.string.tatarka))
        error!!.text = Html.fromHtml(getString(R.string.errorview))
        appintro!!.text = Html.fromHtml(getString(R.string.appintro))
        butterknife!!.text = Html.fromHtml(getString(R.string.butterknife))
        crashlytics!!.text = Html.fromHtml(getString(R.string.crashlytics))

    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun v24Setup() {

        glide!!.text = Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY)
        material!!.text = Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY)
        circularimageview!!.text = Html.fromHtml(getString(R.string.circularimageview), Html.FROM_HTML_MODE_LEGACY)
        tatarka!!.text = Html.fromHtml(getString(R.string.tatarka), Html.FROM_HTML_MODE_LEGACY)
        error!!.text = Html.fromHtml(getString(R.string.errorview), Html.FROM_HTML_MODE_LEGACY)
        appintro!!.text = Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY)
        butterknife!!.text = Html.fromHtml(getString(R.string.butterknife), Html.FROM_HTML_MODE_LEGACY)
        crashlytics!!.text = Html.fromHtml(getString(R.string.crashlytics), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun allThemeLogic() {
        logo!!.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew)
            recreate()
    }
}
