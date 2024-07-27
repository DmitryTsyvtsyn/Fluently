package io.github.dmitrytsyvtsyn.fluently.happening_detail

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

fun LocalDate.toDayMonthYearAbbreviatedString(): String {
    val currentDateTime = this
    return currentDateTime.format(dayMonthYearFormat)
}

private val dayMonthYearFormat = LocalDate.Format {
    dayOfMonth()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
}