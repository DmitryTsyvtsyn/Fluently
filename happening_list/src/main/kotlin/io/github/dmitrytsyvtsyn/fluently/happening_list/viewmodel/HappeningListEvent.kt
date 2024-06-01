package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

internal sealed interface HappeningListEvent {
    data class FetchHappenings(val date: Long) : HappeningListEvent
    data class ChangeDate(val date: Long) : HappeningListEvent
    data class ChangeDateByPageIndex(val index: Int) : HappeningListEvent
    data class ChangePagesByPageIndex(val index: Int) : HappeningListEvent
    data class RemoveHappening(val id: Long, val eventId: Long, val reminderId: Long) : HappeningListEvent
    data class ShowCalendarEvent(val id: Long) : HappeningListEvent
    data class ShowHappeningEditing(val id: Long) : HappeningListEvent
    data object ShowHappeningAdding : HappeningListEvent
    data object ShowDatePicker : HappeningListEvent
    data object SubscribeTimeUpdates : HappeningListEvent
    data object UnsubscribeTimeUpdates : HappeningListEvent
}