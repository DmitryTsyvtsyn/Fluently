package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDateTime

internal data class HappeningDetailState(
    val happening: HappeningModel = HappeningModel(),
    val title: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val titleError: Boolean = false,
    val timeError: Boolean = false,
    val busyState: InterviewEventBusyState = InterviewEventBusyState.NotBusy,
    val hasReminder: Boolean = false,
    val hasPermissionCalendarAllowed: Boolean = false,
)

internal sealed interface InterviewEventBusyState {

    class BusyWithSuggestions(
        val startDateTime: LocalDateTime,
        val endDateTime: LocalDateTime,
        val suggestionRanges: PersistentList<LongRange>
    ) : InterviewEventBusyState

    data object NotBusy : InterviewEventBusyState
}