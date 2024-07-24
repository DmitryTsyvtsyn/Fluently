package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import kotlinx.datetime.LocalDateTime

internal sealed interface HappeningListEvent {

    class FetchHappenings(val date: LocalDateTime) : HappeningListEvent

    class ChangeDate(val date: LocalDateTime) : HappeningListEvent

    class ChangeDateByPageIndex(val index: Int) : HappeningListEvent

    class ChangePagesByPageIndex(val index: Int) : HappeningListEvent

    class RemoveHappening(val happening: HappeningModel) : HappeningListEvent

    class ShowCalendar(val happening: HappeningModel) : HappeningListEvent

    class EditHappening(val happening: HappeningModel) : HappeningListEvent

    data object ShowHappeningAdding : HappeningListEvent

    data object ShowDatePicker : HappeningListEvent

    data object SubscribeTimeUpdates : HappeningListEvent

    data object UnsubscribeTimeUpdates : HappeningListEvent

}