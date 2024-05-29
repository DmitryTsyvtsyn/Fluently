package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

sealed interface HappeningListSideEffect {
    data class ShowCalendarEvent(val id: Long) : HappeningListSideEffect
    data class ShowDetail(val id: Long = -1, val date: Long = System.currentTimeMillis()) : HappeningListSideEffect
    data class ShowDatePicker(val date: Long) : HappeningListSideEffect
}