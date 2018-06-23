package tech.salroid.filmy.custom_adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.ArrayList
import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.data_classes.CharacterDetailsData



class CharacterDetailsActivityAdapter(private val con: Context, ch: List<CharacterDetailsData>, private val ret_size: Boolean?) : RecyclerView.Adapter<CharacterDetailsActivityAdapter.Fo>() {

    private val inflater: LayoutInflater
    private var ch = ArrayList<CharacterDetailsData>()
    private var clickListener: ClickListener? = null

    init {
        inflater = LayoutInflater.from(con)
        this.ch = ArrayList(ch)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Fo {
        val view = inflater.inflate(R.layout.char_custom_row, parent, false)

        return Fo(view)
    }

    override fun onBindViewHolder(holder: Fo, position: Int) {

        val m_name: String?
        val m_profile: String?
        val m_desc: String?
        // String m_id;

        m_name = ch[position].char_movie
        m_profile = ch[position].charmovie_img
        m_desc = ch[position].char_role
        //m_id = ch.get(position).getChar_id();

        // Log.d("webi","charAdapter"+m_id);

        holder.mov_name!!.text = m_name
        holder.mov_char!!.text = m_desc

        try {
            Glide.with(con).load(m_profile).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(holder.mov_img!!)
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

    }

    override fun getItemCount(): Int {
        return if (ret_size!!)
            if (ch.size >= 5) 5 else ch.size
        else
            ch.size
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {

        fun itemClicked(setterGetter: CharacterDetailsData, position: Int)

    }

    inner class Fo(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.movie_poster)
        var mov_img: CircularImageView? = null
        @BindView(R.id.movie_name)
        var mov_name: TextView? = null
        @BindView(R.id.movie_description)
        var mov_char: TextView? = null

        init {
            ButterKnife.bind(this, itemView)


            itemView.setOnClickListener {
                if (clickListener != null) {
                    clickListener!!.itemClicked(ch[position], position)
                }
            }

        }
    }

}
