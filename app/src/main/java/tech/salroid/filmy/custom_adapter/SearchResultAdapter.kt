package tech.salroid.filmy.custom_adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.data_classes.SearchData

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Ramankit Singh (http://github.com/webianks).
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

class SearchResultAdapter(private val fro: Context, data: List<SearchData>) : RecyclerView.Adapter<SearchResultAdapter.Dh>() {

    private val inflater: LayoutInflater
    private var data = ArrayList<SearchData>()
    private var clickListener: ClickListener? = null
    private var query_name: String? = null
    private var query_type: String? = null
    private var query_poster: String? = null
    private var query_id: String? = null
    private var query_date: String? = null
    private var query_extra: String? = null


    init {
        inflater = LayoutInflater.from(fro)
        this.data = ArrayList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Dh {
        val view = inflater.inflate(R.layout.custom_row_search, parent, false)
        return Dh(view)
    }

    override fun onBindViewHolder(holder: Dh, position: Int) {

        query_name = data[position].movie
        query_id = data[position].id
        query_poster = data[position].poster
        query_type = data[position].type
        query_date = data[position].date
        query_extra = data[position].extra

        holder.movie_name!!.text = query_name

        if (query_date != "null")
            holder.date!!.text = query_date
        else {
            holder.date!!.visibility = View.INVISIBLE
        }

        try {
            Glide.with(fro).load(query_poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.movie_poster!!)
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

    }

    override fun getItemCount(): Int {

        return data.size
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {

        fun itemClicked(searchData: SearchData, position: Int)

    }

    inner class Dh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.title)
        var movie_name: TextView? = null
        @BindView(R.id.poster)
        var movie_poster: ImageView? = null
        @BindView(R.id.date)
        var date: TextView? = null
        @BindView(R.id.main)
        var main: FrameLayout? = null

        init {
            ButterKnife.bind(this, itemView)


            main!!.setOnClickListener {
                if (clickListener != null) {
                    clickListener!!.itemClicked(data[position], position)
                }
            }
        }
    }


}
