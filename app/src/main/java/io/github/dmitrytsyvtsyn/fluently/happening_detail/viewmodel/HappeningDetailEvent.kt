package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewEvent

sealed interface HappeningDetailEvent : ViewEvent {

    data class TitleChanged(val title: String) : HappeningDetailEvent

    data class DateChanged(val date: Long) : HappeningDetailEvent

    data class TimeChanged(
        val startHours: Int,
        val startMinutes: Int,
        val endHours: Int,
        val endMinutes: Int
    ) : HappeningDetailEvent

    data class ChangeHasReminder(val hasReminder: Boolean) : HappeningDetailEvent

    data object SaveHappening : HappeningDetailEvent

    data object ShowTimePicker : HappeningDetailEvent

    data object ShowDatePicker : HappeningDetailEvent

    data object Back : HappeningDetailEvent

}