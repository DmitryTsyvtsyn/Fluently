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

    fun plusMinutes(value: Long, difference: Long): Long {
        return value + difference * MINUTE
    }

    fun currentHours(): Int {
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun currentMinutes(): Int {
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.MINUTE)
    }

    fun dateMonthYearMillis(date: Long = System.currentTimeMillis()): Long {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

//    fun dayOfYear(value: Long): Int {
//        calendar.timeInMillis = value
//        return calendar.get(Calendar.DAY_OF_YEAR)
//    }
//
//    fun time(value: Long): Long {
//        calendar.timeInMillis = value
//        val hours = calendar.get(Calendar.HOUR_OF_DAY)
//        val minutes = calendar.get(Calendar.MINUTE)
//        return hours * 60 * MINUTE + minutes * MINUTE
//    }

}