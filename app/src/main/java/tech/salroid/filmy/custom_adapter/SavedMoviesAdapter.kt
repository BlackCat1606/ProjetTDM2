package tech.salroid.filmy.custom_adapter


import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.database.FilmContract

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

class SavedMoviesAdapter(private val context: Context, private var dataCursor: Cursor?) : RecyclerView.Adapter<SavedMoviesAdapter.Vh>() {

    private val inflater: LayoutInflater
    private var clickListener: ClickListener? = null
    private var longclickListener: LongClickListener? = null

    init {

        inflater = LayoutInflater.from(context)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = inflater.inflate(R.layout.custom_row, parent, false)
        return Vh(view)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {

        val movie_title: String
        val imdb_id: String
        val movie_poster: String
        val movie_year: Int

        dataCursor!!.moveToPosition(position)

        val id_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)
        val title_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE)
        val poster_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK)
        val year_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_YEAR)


        movie_title = dataCursor!!.getString(title_index)
        movie_poster = dataCursor!!.getString(poster_index)
        imdb_id = dataCursor!!.getString(id_index)
        movie_year = dataCursor!!.getInt(year_index)

        holder.title!!.text = movie_title

        try {
            Glide.with(context).load(movie_poster).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.poster!!)

        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

    }


    override fun getItemCount(): Int {
        return if (dataCursor == null) 0 else dataCursor!!.count
    }

    fun swapCursor(cursor: Cursor?): Cursor? {
        if (dataCursor === cursor) {
            return null
        }
        val oldCursor = dataCursor
        this.dataCursor = cursor
        if (cursor != null) {
            this.notifyDataSetChanged()
        }
        return oldCursor
    }

    fun setLongClickListener(longclickListener: LongClickListener) {
        this.longclickListener = longclickListener
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {

        fun itemClicked(id: String, title: String)

    }

    interface LongClickListener {
        fun itemLongClicked(cursor: Cursor?, position: Int)

    }

    inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.title)
        var title: TextView? = null
        @BindView(R.id.poster)
        var poster: ImageView? = null
        @BindView(R.id.main)
        var main: FrameLayout? = null


        init {
            ButterKnife.bind(this, itemView)

            ButterKnife.bind(this, itemView)

            main!!.setOnClickListener {
                dataCursor!!.moveToPosition(position)

                val movie_id_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)
                val movie_title_index = dataCursor!!.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE)

                if (clickListener != null) {
                    clickListener!!.itemClicked(dataCursor!!.getString(movie_id_index),
                            dataCursor!!.getString(movie_title_index))
                }
            }

            main!!.setOnLongClickListener {
                dataCursor!!.moveToPosition(position)


                if (longclickListener != null) {
                    longclickListener!!.itemLongClicked(dataCursor, position)
                }

                true
            }

        }
    }

}