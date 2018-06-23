package tech.salroid.filmy.network_stuff

/**
 * Created by BlackCat on 5/20/2018.
 */
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

import tech.salroid.filmy.FilmyApplication


class TmdbVolleySingleton private constructor() {
    val requestQueue: RequestQueue


    init {
        requestQueue = Volley.newRequestQueue(FilmyApplication.context)
    }

    companion object {


        var instance: TmdbVolleySingleton? = null

    }
}