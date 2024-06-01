package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository

internal sealed interface HappeningListSideEffect {
    data class ShowCalendarEvent(val id: Long) : HappeningListSideEffect
    data class ShowDetail(val id: Long = -1, val date: Long = CalendarRepository.nowDate()) : HappeningListSideEffect
    data class ShowDatePicker(val date: Long) : HappeningListSideEffect
}