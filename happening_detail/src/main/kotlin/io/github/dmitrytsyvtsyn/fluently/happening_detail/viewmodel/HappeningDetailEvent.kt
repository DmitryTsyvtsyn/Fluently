package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal sealed interface HappeningDetailEvent {

    class Init(val id: IdLong, val initialDateTime: LocalDateTime) : HappeningDetailEvent

    class TitleChanged(val title: String) : HappeningDetailEvent

    class DateChanged(val dateTime: LocalDateTime) : HappeningDetailEvent

    class TimeChanged(
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : HappeningDetailEvent

    class ChangeHasReminder(val hasReminder: Boolean) : HappeningDetailEvent

    class ChangeCalendarPermissionsStatus(val allowed: Boolean) : HappeningDetailEvent

    data object SaveHappening : HappeningDetailEvent

    data object ShowTimePicker : HappeningDetailEvent

    data object ShowDatePicker : HappeningDetailEvent

    data object Back : HappeningDetailEvent

}