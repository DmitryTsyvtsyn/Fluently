package io.github.dmitrytsyvtsyn.interfunny.interview_event_list

import java.text.SimpleDateFormat

object CalendarRepository {

    private val dateMonthFormat = SimpleDateFormat("dd MMM")
    private val dateMonthWeekFormat = SimpleDateFormat("dd MMM, EEE")
    private val dateMonthYearFormat = SimpleDateFormat("dd MMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")

    private const val MINUTE = 60 * 1000

    fun formatDateMonth(time: Long): String {
        return dateMonthFormat.format(time)
    }

    fun formatDateMonthWeek(time: Long): String {
        return dateMonthWeekFormat.format(time)
    }

    fun formatDateMonthYear(time: Long): String {
        return dateMonthYearFormat.format(time)
    }

    fun formatHoursMinutes(time: Long): String {
        return timeFormat.format(time)
    }

    fun minutesInMillis(value: Long): Long {
        return MINUTE * value
    }

}