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
import tech.salroid.filmy.data_classes.CastDetailsData



public class CastAdapter(private val context: Context, cast: List<CastDetailsData>, private val ret_size: Boolean?) : RecyclerView.Adapter<CastAdapter.Ho>() {
    private var cast = ArrayList<CastDetailsData>()
    private var clickListener: ClickListener? = null

    init {
        this.cast = ArrayList(cast)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Ho {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.cast_custom_row, parent, false)
        return Ho(view)
    }

    override fun onBindViewHolder(holder: Ho, position: Int) {

        val ct_name = cast[position].cast_name
        val ct_desc = cast[position].cast_character
        val ct_profile = cast[position].cast_profile
        val ct_id = cast[position].cast_id

        holder.cast_name!!.text = ct_name
        holder.cast_description!!.text = ct_desc

        try {
            Glide.with(context)
                    .load(ct_profile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.cast_poster!!)
        } catch (e: Exception) {
        }

    }


    override fun getItemCount(): Int {

        return if (ret_size!!)
            if (cast.size >= 5) 5 else cast.size
        else
            cast.size

    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {

        fun itemClicked(setterGetter: CastDetailsData, position: Int, view: View)

    }

    inner class Ho(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.cast_name)
        var cast_name: TextView? = null
        @BindView(R.id.cast_description)
        var cast_description: TextView? = null
        @BindView(R.id.cast_poster)
        var cast_poster: CircularImageView? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener { view ->
                if (clickListener != null) {
                    clickListener!!.itemClicked(cast[position], position, view)
                }
            }
        }
    }


}