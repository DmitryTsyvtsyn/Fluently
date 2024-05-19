package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList

@Stable
sealed interface InterviewEventListItemState {
    data class Title(val value: String) : InterviewEventListItemState
    data class Content(val model: InterviewEventModel) : InterviewEventListItemState
    data class Timeline(val startDate: Long, val endDate: Long) : InterviewEventListItemState
}

data class InterviewEventListState(
    val date: Long,
    val prevDate: Long,
    val nextDate: Long,
    val totalEvents: PersistentList<InterviewEventModel>,
    val filteredEvents: PersistentList<InterviewEventListItemState>
)