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
import tech.salroid.filmy.data_classes.CrewDetailsData



class CrewAdapter(private val context: Context, crew: List<CrewDetailsData>, private val ret_size: Boolean?) : RecyclerView.Adapter<CrewAdapter.Ho>() {
    private var crew = ArrayList<CrewDetailsData>()
    private var clickListener: ClickListener? = null

    init {
        this.crew = ArrayList(crew)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Ho {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.crew_custom_row, parent, false)
        return Ho(view)
    }

    override fun onBindViewHolder(holder: Ho, position: Int) {

        val ct_name = crew[position].crew_name
        val ct_desc = crew[position].crew_job
        val ct_profile = crew[position].crew_profile
        val ct_id = crew[position].crew_id

        holder.crew_name!!.text = ct_name
        holder.crew_description!!.text = ct_desc

        try {
            Glide.with(context)
                    .load(ct_profile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.crew_poster!!)
        } catch (e: Exception) {
        }

    }


    override fun getItemCount(): Int {

        return if (ret_size!!)
            if (crew.size >= 5) 5 else crew.size
        else
            crew.size

    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {

        fun itemClicked(setterGetter: CrewDetailsData, position: Int, view: View)

    }

    inner class Ho(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.crew_name)
        var crew_name: TextView? = null
        @BindView(R.id.crew_description)
        var crew_description: TextView? = null
        @BindView(R.id.crew_poster)
        var crew_poster: CircularImageView? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener { view ->
                if (clickListener != null) {
                    clickListener!!.itemClicked(crew[position], position, view)
                }
            }
        }
    }


}