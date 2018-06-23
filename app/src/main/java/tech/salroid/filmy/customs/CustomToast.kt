package tech.salroid.filmy.customs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import tech.salroid.filmy.R


object CustomToast {

    fun show(context: Context?, message: String, showImage: Boolean) {


        if (context != null) {

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val layout = inflater.inflate(R.layout.custom_toast, null)

            val text = layout.findViewById<TextView>(R.id.text) as TextView
            text.text = message

            val imageView = layout.findViewById<ImageView>(R.id.image) as ImageView

            if (!showImage)
                imageView.visibility = View.GONE

            layout.requestLayout()

            val toast = Toast(context.applicationContext)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        }


    }
}
