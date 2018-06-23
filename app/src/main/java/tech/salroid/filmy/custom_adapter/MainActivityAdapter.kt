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



class MainActivityAdapter(private val context: Context, private var dataCursor: Cursor?) : RecyclerView.Adapter<MainActivityAdapter.Vh>() {

    private val inflater: LayoutInflater
    private var clickListener: ClickListener? = null

    init {

        inflater = LayoutInflater.from(context)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = inflater.inflate(R.layout.custom_row, parent, false)
        return Vh(view)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {

        val movie_title: String
        val movie_poster: String
        val imdb_id: String
        val movie_year: Int


        dataCursor!!.moveToPosition(position)


        val id_index = dataCursor!!.getColumnIndex(FilmContract.MoviesEntry.MOVIE_ID)
        val title_index = dataCursor!!.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
        val poster_index = dataCursor!!.getColumnIndex(FilmContract.MoviesEntry.MOVIE_POSTER_LINK)
        val year_index = dataCursor!!.getColumnIndex(FilmContract.MoviesEntry.MOVIE_YEAR)

        movie_title = dataCursor!!.getString(title_index)
        movie_poster = dataCursor!!.getString(poster_index)
        imdb_id = dataCursor!!.getString(id_index)
        movie_year = dataCursor!!.getInt(year_index)


        holder.title!!.text = movie_title + " / " + movie_year
        //holder.year.setText(String.valueOf(movie_year));

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

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    interface ClickListener {

        fun itemClicked(cursor: Cursor?)

    }

    inner class Vh
    // TextView year;

    (itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.title)
        var title: TextView? = null
        @BindView(R.id.poster)
        var poster: ImageView? = null
        @BindView(R.id.main)
        var main: FrameLayout? = null

        init {
            ButterKnife.bind(this, itemView)


            //year = (TextView) itemView.findViewById(R.id.movie_year);


            main!!.setOnClickListener {
                dataCursor!!.moveToPosition(position)

                if (clickListener != null) {
                    clickListener!!.itemClicked(dataCursor)
                }
            }
        }
    }

}
