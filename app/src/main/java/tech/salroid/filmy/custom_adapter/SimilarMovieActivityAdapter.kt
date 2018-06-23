package tech.salroid.filmy.custom_adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mikhaellopez.circularimageview.CircularImageView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.data_classes.CastDetailsData
import tech.salroid.filmy.data_classes.SimilarMoviesData
import tech.salroid.filmy.fragment.SimilarFragment

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

class SimilarMovieActivityAdapter(private val context: Context, similar: List<SimilarMoviesData>, private val ret_size: Boolean?) : RecyclerView.Adapter<SimilarMovieActivityAdapter.Ho>() {
    private var similar = ArrayList<SimilarMoviesData>()
    private var clickListener: SimilarMovieActivityAdapter.ClickListener? = null


    init {
        this.similar = ArrayList(similar)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Ho {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.similar_custom_row, parent, false)
        return Ho(view)
    }

    override fun onBindViewHolder(holder: Ho, position: Int) {

        val similar_title = similar[position].movie_title
        val similar_banner = similar[position].movie_banner
        val similar_id = similar[position].movie_id


        holder.title!!.text = similar_title

        try {
            Glide.with(context)
                    .load(similar_banner)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.poster!!)
        } catch (e: Exception) {
        }

    }


    override fun getItemCount(): Int {
        return similar.size

    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    interface ClickListener {

        fun itemClicked(setterGetter: SimilarMoviesData, position: Int, view: View)

    }

    inner class Ho(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.title)
        var title: TextView? = null
        @BindView(R.id.poster)
        var poster: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener { view ->
                if (clickListener != null) {
                    clickListener!!.itemClicked(similar[position], position, view)
                }
            }
        }
    }


}


