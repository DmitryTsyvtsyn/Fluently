package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

internal sealed interface HappeningListEvent {

    class FetchHappenings(val date: Long) : HappeningListEvent

    class ChangeDate(val date: Long) : HappeningListEvent

    class ChangeDateByPageIndex(val index: Int) : HappeningListEvent

    class ChangePagesByPageIndex(val index: Int) : HappeningListEvent

    class RemoveHappening(val id: Long, val eventId: Long, val reminderId: Long) : HappeningListEvent

    class ShowCalendarEvent(val id: Long) : HappeningListEvent

    class ShowHappeningEditing(val id: Long) : HappeningListEvent

    data object ShowHappeningAdding : HappeningListEvent

    data object ShowDatePicker : HappeningListEvent

    data object SubscribeTimeUpdates : HappeningListEvent

    data object UnsubscribeTimeUpdates : HappeningListEvent

}