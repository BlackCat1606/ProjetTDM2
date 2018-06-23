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
import tech.salroid.filmy.data_classes.CastDetailsData
import tech.salroid.filmy.parser.MovieDetailsActivityParseWork

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

class FullCastActivity : AppCompatActivity(), CastAdapter.ClickListener {

    @BindView(R.id.toolbar) internal var toolbar: Toolbar? = null
    @BindView(R.id.full_cast_recycler) internal var full_cast_recycler: RecyclerView? = null


    private var cast_result: String? = null
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

        full_cast_recycler!!.layoutManager = LinearLayoutManager(this@FullCastActivity)


        val intent = intent
        if (intent != null) {
            cast_result = intent.getStringExtra("cast_json")
            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.title = intent.getStringExtra("toolbar_title")
            }
        }


        val par = MovieDetailsActivityParseWork(this, cast_result!!)
        val cast_list = par.parse_cast()
        val full_cast_adapter = CastAdapter(this, cast_list, false)
        full_cast_adapter.setClickListener(this)
        full_cast_recycler!!.adapter = full_cast_adapter

    }

    override fun itemClicked(setterGetter: CastDetailsData, position: Int, view: View) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("id", setterGetter.cast_id)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            val p1 = Pair.create(view.findViewById<View>(R.id.cast_poster), "profile")
            val p2 = Pair.create(view.findViewById<View>(R.id.cast_name), "name")

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