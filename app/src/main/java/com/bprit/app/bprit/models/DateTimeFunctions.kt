package com.bprit.app.bprit.models

import java.lang.Exception
import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat


/**
 * Date and time fuctions
 */
class DateTimeFunctions {

    /**
     * @return Localized calendar with Danish settings
     */
    fun getLocalizedCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.minimalDaysInFirstWeek = 4
        calendar.firstDayOfWeek = Calendar.MONDAY
        return calendar
    }

    /**
     * @return Current date formatted as 'yyyy-MM-dd'.
     */
    fun getCurrentDate(): String {
        val calendar = getLocalizedCalendar()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        simpleDateFormat.calendar = calendar
        return simpleDateFormat.format(calendar.time)
    }

    /**
     * Make date beautiful
     * @param date date formatted as 'yyyy-MM-dd'
     * @return date formatted as 'dd.MM.yyyy'
     */
    fun beautifyDate(date: String): String {
        var beautifulDate = date

        if (date != "") {
            try {
                val calendar = getLocalizedCalendar()
                val simpleDateFormatBefore = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                simpleDateFormatBefore.calendar = calendar
                calendar.time = simpleDateFormatBefore.parse(date)
                val simpleDateFormatAfter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                simpleDateFormatAfter.calendar = calendar
                beautifulDate = simpleDateFormatAfter.format(calendar.time)
            } catch (e: ParseException) {
//                Crashlytics.logException(e)
            }
        }

        return beautifulDate
    }

    /**
     * Make date beautiful
     * @param date date object
     * @return date formatted as 'dd.MM.yyyy'
     */
    fun beautifyDate(date: Date?): String? {
        var beautifulDate = ""

        // TODO BB 2018-10-27. Test that this is working.

        date?.let {d ->
            try {
                val calendar = getLocalizedCalendar()
                calendar.time = d
                val simpleDateFormatAfter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                simpleDateFormatAfter.calendar = calendar
                beautifulDate = simpleDateFormatAfter.format(calendar.time)
            } catch (e: ParseException) {
//                Crashlytics.logException(e)
            }
        }

        return beautifulDate
    }

    /**
     * Get date from string
     * @param date date string formatted as 'yyyy-MM-dd'
     * @return date date object
     */
    fun getDateFromString(date: String) : Date? {
        var d: Date? = null

        try {
            val calendar = getLocalizedCalendar()
            val simpleDateFormatBefore = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            simpleDateFormatBefore.calendar = calendar
            d = simpleDateFormatBefore.parse(date)
        } catch (e: Exception) {

        }

        return d
    }
}