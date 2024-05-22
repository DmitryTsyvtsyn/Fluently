package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states

import kotlinx.collections.immutable.PersistentList

data class InterviewListState(
    val date: Long,
    val prevDate: Long,
    val nextDate: Long,
    val totalEvents: PersistentList<InterviewModel>,
    val filteredEvents: PersistentList<InterviewListItemState>
)