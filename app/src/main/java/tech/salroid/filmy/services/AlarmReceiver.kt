package tech.salroid.filmy.services


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat


import java.util.Calendar

import tech.salroid.filmy.R
import tech.salroid.filmy.activities.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    private val NOTIF_ID_ONETIME = 100
    private val NOTIF_ID_REPEATING = 101

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        var title = if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) "One Time Alarm" else "Repeating Alarm"
        val notifId = if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) NOTIF_ID_ONETIME else NOTIF_ID_REPEATING

        title = context.resources.getString(R.string.app_name)

        showAlarmNotification(context, title, message, notifId)
    }

    private fun showAlarmNotification(context: Context, title: String, message: String, notifId: Int) {
        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

        notificationManagerCompat.notify(notifId, builder.build())
    }

    fun setOneTimeAlarm(context: Context, type: String, date: String, time: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val dateArray = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val requestCode = NOTIF_ID_ONETIME
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun setRepeatingAlarm(context: Context, type: String, time: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        if (calendar.before(Calendar.getInstance())) calendar.add(Calendar.DATE, 1)

        val requestCode = NOTIF_ID_REPEATING
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelAlarm(context: Context, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val requestCode = if (type.equals(TYPE_ONE_TIME, ignoreCase = true)) NOTIF_ID_ONETIME else NOTIF_ID_REPEATING
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        val TYPE_ONE_TIME = "OneTimeAlarm"
        val TYPE_REPEATING = "RepeatingAlarm"
        val EXTRA_MESSAGE = "message"
        val EXTRA_TYPE = "type"
    }
}
