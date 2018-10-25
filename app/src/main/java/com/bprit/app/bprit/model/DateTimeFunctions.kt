package com.bprit.app.bprit.model

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
}