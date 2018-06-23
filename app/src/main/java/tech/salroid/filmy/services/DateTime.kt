package tech.salroid.filmy.services

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date


object DateTime {

    private fun formatDate(date: String, format: String): String {
        var result = ""

        val old = SimpleDateFormat("yyyy-MM-dd")
        try {
            val oldDate = old.parse(date)
            val newFormat = SimpleDateFormat(format)
            result = newFormat.format(oldDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }

    fun getShortDate(date: String): String {
        return formatDate(date, "dd MMMM yyyy")
    }

    fun getLongDate(date: String): String {
        return formatDate(date, "EEEE, MMM d, yyyy")
    }
}
