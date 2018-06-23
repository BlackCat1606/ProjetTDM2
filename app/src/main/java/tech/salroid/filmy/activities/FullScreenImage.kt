package tech.salroid.filmy.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget

import butterknife.BindView
import butterknife.ButterKnife
import tech.salroid.filmy.R
import tech.salroid.filmy.customs.BreathingProgress




class FullScreenImage : AppCompatActivity() {

    @BindView(R.id.cen_img)
    internal var centreimg: ImageView? = null
    @BindView(R.id.breathingProgress)
    internal var breathingProgress: BreathingProgress? = null

    private var image_url: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        ButterKnife.bind(this)

        breathingProgress!!.visibility = View.VISIBLE


        val intent = intent
        if (intent != null) {
            image_url = intent.getStringExtra("img_url")
        }
        try {

            Glide.with(this)
                    .load(image_url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            centreimg!!.setImageBitmap(resource)
                            breathingProgress!!.visibility = View.INVISIBLE
                        }
                    })
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}