package io.github.dmitrytsyvtsyn.fluently.happening_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import kotlin.math.roundToInt

@Composable
fun formatFloatingHours(startDate: Long, endDate: Long): String {
    val hours = (endDate - startDate) / 3_600_000f

    if (hours < 1) {
        return stringResource(id = R.string.minute_suffix, (hours * 60).toInt())
    }

    val divider = (hours * 10).toInt() % 10
    if (divider == 0) {
        return stringResource(id = R.string.hour_suffix, hours.toInt())
    }

    if (divider == 5) {
        return stringResource(id = R.string.hour_suffix, "${hours.toInt()}.5")
    }

    return stringResource(id = R.string.hour_suffix, hours.roundToInt())
}

@Composable
fun formatDate(date: Long, nowDate: Long): String {
    val formattedDate = CalendarRepository.formatDateMonth(date)
    val formattedNowDate = CalendarRepository.formatDateMonth(nowDate)
    return if (formattedNowDate == formattedDate) {
        stringResource(id = R.string.today_day)
    } else {
        formattedDate
    }
}