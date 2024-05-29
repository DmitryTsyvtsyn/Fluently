package io.github.dmitrytsyvtsyn.fluently.happening_list

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.collection.IntIntPair
import java.text.SimpleDateFormat
import java.util.Locale

object CalendarRepository {

    @SuppressLint("ConstantLocale")
    private val locale = Locale.getDefault()
    private val dateMonthFormat = SimpleDateFormat("dd MMMM", locale)
    private val dateMonthYearFormat = SimpleDateFormat("dd MMM yyyy", locale)
    private val briefDateMonthYearFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    private val timeFormat = SimpleDateFormat("HH:mm", locale)
    private val calendar = Calendar.getInstance()

    private const val SECOND = 1_000L
    private const val MINUTE = 60 * SECOND
    private const val DAY = 24 * 60 * MINUTE

    fun formatDateMonth(time: Long): String {
        return dateMonthFormat.format(time)
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

    fun timeFromDate(date: Long): Int {
        calendar.timeInMillis = date
        return calendar.get(Calendar.MILLISECONDS_IN_DAY)
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

    fun hoursMinutes(date: Long): IntIntPair {
        calendar.timeInMillis = date
        return IntIntPair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    fun timeFromHoursAndMinutes(hours: Int, minutes: Int): Int {
        return hours * 60 + minutes
    }

    fun nowDate(): Long {
        return System.currentTimeMillis()
    }

    fun compareDates(date1: Long, date2: Long): Boolean {
        calendar.timeInMillis = date1
        val days1 = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = date2
        val days2 = calendar.get(Calendar.DAY_OF_YEAR)

        return days1 == days2
    }

    fun calculateMillisForNextMinute(time: Long): Long {
        calendar.timeInMillis = time
        calendar.add(Calendar.MINUTE, 1)
        calendar.set(Calendar.SECOND, 0)
        val nextTime = calendar.timeInMillis
        return nextTime - time
    }

}