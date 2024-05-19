package io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.states

import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import kotlinx.collections.immutable.PersistentList

data class InterviewEventDetailState(
    val id: Long = -1,
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

sealed interface InterviewEventBusyState {
    data class BusyWithSuggestions(
        val startDate: Long,
        val endDate: Long,
        val scheduledStates: PersistentList<InterviewEventModel>,
        val suggestionRanges: PersistentList<LongRange>
    ) : InterviewEventBusyState
    data object NotBusy : InterviewEventBusyState
}