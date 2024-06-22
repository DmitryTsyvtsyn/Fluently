package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository

internal sealed interface HappeningListSideEffect {

    class ShowCalendarEvent(val id: Long) : HappeningListSideEffect

    class ShowDetail(val id: Long = -1, val date: Long = CalendarRepository.nowDate()) : HappeningListSideEffect

    class ShowDatePicker(val date: Long) : HappeningListSideEffect

}