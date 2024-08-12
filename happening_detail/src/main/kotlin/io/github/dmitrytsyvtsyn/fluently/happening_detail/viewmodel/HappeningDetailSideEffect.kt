package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal sealed interface HappeningDetailSideEffect {

    class DatePicker(
        val initialDate: LocalDate,
        val minDate: LocalDate
    ) : HappeningDetailSideEffect

    class TimePicker(
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : HappeningDetailSideEffect

    data object Back : HappeningDetailSideEffect

    data object CheckCalendarPermission : HappeningDetailSideEffect

}