package tech.salroid.filmy.services

import android.content.ComponentName
import android.content.Context
import android.util.Log

import me.tatarka.support.job.JobInfo
import me.tatarka.support.job.JobScheduler


class FilmyJobScheduler(private val context: Context) {
    private val jobScheduler: JobScheduler
    private val JOB_ID = 456

    init {
        jobScheduler = JobScheduler.getInstance(context)

    }

    fun createJob() {


        val jobBuilder = JobInfo.Builder(JOB_ID, ComponentName(context, FilmyJobService::class.java))

        //PersistableBundle persistableBundle = new PersistableBundle();

        jobBuilder.setPeriodic(SYNC_INTERVAL)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)

        jobScheduler.schedule(jobBuilder.build())


    }

    companion object {


        val SYNC_INTERVAL: Long = 21600000

        val time = System.currentTimeMillis()
    }


}