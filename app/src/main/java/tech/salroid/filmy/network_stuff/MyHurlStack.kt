package tech.salroid.filmy.network_stuff

/**
 * Created by BlackCat on 5/20/2018.
 */
import com.android.volley.toolbox.HurlStack

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.FilmyApplication
import tech.salroid.filmy.R


class MyHurlStack : HurlStack() {

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {

        val api_key = BuildConfig.TMDB_API_KEY
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("trakt-api-version", "2")
        //connection.setRequestProperty("trakt-api-key", FilmyApplication.getContext().getString(R.string.api_key));

        return connection
    }
}