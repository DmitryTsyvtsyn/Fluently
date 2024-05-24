package io.github.dmitrytsyvtsyn.interfunny.interview_list

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

object CalendarRepository {

    @SuppressLint("ConstantLocale")
    private val locale = Locale.getDefault()
    private val dateMonthFormat = SimpleDateFormat("dd MMMM", locale)
    private val dateMonthWeekFormat = SimpleDateFormat("dd MMM, EEE", locale)
    private val dateMonthYearFormat = SimpleDateFormat("dd MMM yyyy", locale)
    private val briefDateMonthYearFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    private val timeFormat = SimpleDateFormat("HH:mm", locale)
    private val calendar = Calendar.getInstance()

    private const val MINUTE = 60 * 1000L
    private const val DAY = 24 * 3600 * 1000

    fun formatDateMonth(time: Long): String {
        return dateMonthFormat.format(time)
    }

    fun formatDateMonthWeek(time: Long): String {
        return dateMonthWeekFormat.format(time)
    }

    fun formatDateMonthYear(time: Long): String {
        return dateMonthYearFormat.format(time)
    }

    fun formatDateMonthYearBrief(time: Long): String {
        return briefDateMonthYearFormat.format(time)
    }

    fun formatHoursMinutes(time: Long): String {
        return timeFormat.format(time)
    }

    fun minutesInMillis(value: Long): Long {
        return MINUTE * value
    }

    fun minusMinutes(value: Long, difference: Long): Long {
        return value - difference * MINUTE
    }

    fun plusMinutes(date: Long, difference: Long): Long {
        return date + difference * MINUTE
    }

    fun plusDays(date: Long = System.currentTimeMillis(), days: Int = 1): Long {
        return date + days * DAY
    }

    fun minusDays(date: Long = System.currentTimeMillis(), days: Int = 1): Long {
        return date - days * DAY
    }

    fun matchTimeWithDate(time: Long, date: Long): Long {
        calendar.timeInMillis = date
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = time
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear)
        return calendar.timeInMillis
    }

    fun matchDateWithTime(date: Long, time: Long): Long {
        calendar.timeInMillis = time
        val millis = calendar.get(Calendar.MILLISECONDS_IN_DAY)

        calendar.timeInMillis = date
        calendar.set(Calendar.MILLISECONDS_IN_DAY, millis)
        return calendar.timeInMillis
    }

    fun currentTime(): Int {
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    fun matchDateWithHoursAndMinutes(date: Long, hours: Int, minutes: Int): Long {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        return calendar.timeInMillis
    }

    fun dateMonthYearMillis(date: Long = System.currentTimeMillis()): Long {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun dateRangeInDays(date: Long, days: Int = 1): LongRange {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, days)
        val endDate = calendar.timeInMillis

        return startDate until endDate
    }

    fun nowDate(): Long {
        return System.currentTimeMillis()
    }

}