package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import kotlinx.collections.immutable.PersistentList

internal data class HappeningDetailState(
    val id: Long = 0,
    val eventId: Long = -1,
    val reminderId: Long = -1,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val titleError: Boolean = false,
    val timeError: Boolean = false,
    val busyState: InterviewEventBusyState = InterviewEventBusyState.NotBusy,
    val hasReminder: Boolean = false,
)

internal sealed interface InterviewEventBusyState {
    data class BusyWithSuggestions(
        val startDate: Long,
        val endDate: Long,
        val scheduledStates: PersistentList<io.github.dmitrytsyvtsyn.fluently.data.HappeningModel>,
        val suggestionRanges: PersistentList<LongRange>
    ) : InterviewEventBusyState
    data object NotBusy : InterviewEventBusyState
}