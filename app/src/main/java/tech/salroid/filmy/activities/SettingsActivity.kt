package tech.salroid.filmy.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.utility.FontUtility


class SettingsActivity : AppCompatActivity() {

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

        setContentView(R.layout.activity_settings)
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

        fragmentManager.beginTransaction().replace(R.id.container,
                MyPreferenceFragment()).commit()


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

    class MyPreferenceFragment : PreferenceFragment() {

        private var imagePref: SwitchPreference? = null
        private var darkPref: CheckBoxPreference? = null

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)


            val my_prefrence = PreferenceManager.getDefaultSharedPreferences(activity).edit()

            imagePref = findPreference("imagequality") as SwitchPreference
            imagePref!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val quality: String

                val switchPreference = preference as SwitchPreference

                if (!switchPreference.isChecked) {
                    quality = "w1000"
                } else {
                    quality = "w780"
                }


                my_prefrence.putString("image_quality", quality)
                my_prefrence.apply()

                true
            }


            darkPref = findPreference("dark") as CheckBoxPreference
            darkPref!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                recreateActivity()

                true
            }

            val liscence = findPreference("license") as Preference
            liscence.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(activity, License::class.java))
                true
            }


            val share = findPreference("Share") as Preference
            share.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val app_share_details = resources.getString(R.string.app_share_link)
                if (app_share_details != null) {
                    val myIntent = Intent(Intent.ACTION_SEND)
                    myIntent.type = "text/plain"
                    myIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome movie app.\n*filmy*\n$app_share_details")
                    startActivity(Intent.createChooser(myIntent, "Share with"))
                }
                true
            }


            val about = findPreference("About") as Preference
            about.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(activity, AboutActivity::class.java))
                true
            }

        }

        private fun recreateActivity() {
            activity.recreate()
        }

    }

}