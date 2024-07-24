package io.github.dmitrytsyvtsyn.fluently.happening_detail

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

fun LocalDateTime.toDayMonthYearString(): String {
    val currentDateTime = this
    return currentDateTime.format(dayMonthYearFormat)
}

private val dayMonthYearFormat = LocalDateTime.Format {
    dayOfMonth()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
}