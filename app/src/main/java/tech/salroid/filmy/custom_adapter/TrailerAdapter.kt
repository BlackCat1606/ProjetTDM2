package tech.salroid.filmy.custom_adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R

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

class TrailerAdapter(private val trailers: Array<String>, private val trailers_name: Array<String>, private val context: Context) : RecyclerView.Adapter<TrailerAdapter.VH>() {
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.single_trailer_view, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val trailer_id = trailers[position]
        val name = trailers_name[position]
        val trailer_thumbnail = (context.resources.getString(R.string.trailer_img_prefix) + trailer_id
                + context.resources.getString(R.string.trailer_img_suffix))

        try {
            Glide.with(context)
                    .load(trailer_thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.youtube_thumbnail!!)
        } catch (e: Exception) {
        }


        holder.trailerTitle!!.text = name


    }

    override fun getItemCount(): Int {
        return trailers.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.detail_youtube)
        var youtube_thumbnail: ImageView? = null
        @BindView(R.id.trailerTitle)
        var trailerTitle: TextView? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener {
                if (onItemClickListener != null)
                    onItemClickListener!!.itemClicked(trailers[adapterPosition])
            }
        }
    }

    interface OnItemClickListener {
        fun itemClicked(trailerId: String)
    }


}
