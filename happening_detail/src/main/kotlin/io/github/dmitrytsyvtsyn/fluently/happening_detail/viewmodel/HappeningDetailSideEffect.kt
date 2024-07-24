package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal sealed interface HappeningDetailSideEffect {

    class DatePicker(val dateTime: LocalDateTime) : HappeningDetailSideEffect

    class TimePicker(
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : HappeningDetailSideEffect

    data object Back : HappeningDetailSideEffect

    data object CheckCalendarPermission : HappeningDetailSideEffect

}