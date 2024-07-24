package io.github.dmitrytsyvtsyn.fluently.happening_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char

@Composable
fun DateTimePeriod.toHoursMinutesString(): String {
    val period = this
    val hours = period.hours
    if (hours < 1) {
        return stringResource(id = R.string.minute_suffix, period.minutes)
    }

    val minutes = period.minutes
    if (minutes == 30) { // half an hour
        return stringResource(id = R.string.hour_suffix, "$hours.5")
    }

    return stringResource(id = R.string.hour_suffix, hours)
}

@Composable
fun LocalDateTime.toDateMonthString(nowDateTime: LocalDateTime): String {
    val currentDateTime = this
    if (currentDateTime.date == nowDateTime.date) {
        return stringResource(id = R.string.today_day)
    }

    return currentDateTime.date.format(format2)
}

//private val format = LocalDate.Format {
//    byUnicodePattern("dd MMMM")
//}

private val format2 = LocalDate.Format {
    dayOfMonth()
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}