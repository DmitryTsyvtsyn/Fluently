package io.github.dmitrytsyvtsyn.fluently.happening_detail.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import io.github.dmitrytsyvtsyn.fluently.happening_detail.R
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@Composable
fun LocalDate.toDayMonthYearAbbreviatedString(): String {
    val currentDateTime = this
    val newMonthNames = MonthNames(stringArrayResource(id = R.array.month_names_abbreviated).toList())
    if (monthNames != newMonthNames) {
        monthNames = newMonthNames
        dayMonthYearFormat = createDateMonthYearFormat()
    }
    return currentDateTime.format(dayMonthYearFormat)
}

private var monthNames = MonthNames.ENGLISH_ABBREVIATED
private var dayMonthYearFormat = createDateMonthYearFormat()

private fun createDateMonthYearFormat() = LocalDate.Format {
    dayOfMonth(Padding.NONE)
    char(' ')
    monthName(monthNames)
    char(' ')
    year()
}