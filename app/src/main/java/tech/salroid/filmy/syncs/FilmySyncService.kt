package tech.salroid.filmy.syncs

/**
 * Created by BlackCat on 5/20/2018.
 */
import android.app.Service
import android.content.Intent
import android.os.IBinder


class FilmySyncService : Service() {

    override fun onCreate() {
        // Log.d("FilmySyncService", "FilmySyncService");
        synchronized(sSyncAdapterLock) {
            if (sFilmySyncAdapter == null) {
                sFilmySyncAdapter = FilmySyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return sFilmySyncAdapter!!.syncAdapterBinder
    }

    companion object {

        private val sSyncAdapterLock = Any()
        private var sFilmySyncAdapter: FilmySyncAdapter? = null
    }
}
