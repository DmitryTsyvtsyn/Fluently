package io.github.dmitrytsyvtsyn.fluently.core.data

import android.annotation.SuppressLint
import android.icu.util.Calendar
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Locale

object CalendarRepository {

    @SuppressLint("ConstantLocale")
    private val locale = Locale.getDefault()
    private val briefDateMonthYearFormat = SimpleDateFormat("dd.MM.yyyy", locale)
    private val timeFormat = SimpleDateFormat("HH:mm", locale)
    private val calendar = Calendar.getInstance()

    fun nowDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    fun formatDateMonthYearBrief(time: Long): String {
        return briefDateMonthYearFormat.format(time)
    }

    fun formatHoursMinutes(time: Long): String {
        return timeFormat.format(time)
    }

    fun currentTime(): Int {
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    fun dateMonthYearMillis(date: Long = System.currentTimeMillis()): Long {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun nowDate(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

}