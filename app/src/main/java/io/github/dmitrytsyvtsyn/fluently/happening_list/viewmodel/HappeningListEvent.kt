package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

sealed interface HappeningListEvent {
    data class ChangeDate(val date: Long) : HappeningListEvent
    data class ChangeDateByPageIndex(val index: Int) : HappeningListEvent
    data class ChangePagesByPageIndex(val index: Int) : HappeningListEvent
    data class RemoveHappening(val id: Long, val eventId: Long, val reminderId: Long) :
        HappeningListEvent
    data class ShowHappeningEditing(val id: Long) : HappeningListEvent
    data object ShowHappeningAdding : HappeningListEvent
    data object ShowDatePicker : HappeningListEvent
}