package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states

import kotlinx.collections.immutable.PersistentList

data class InterviewListState(
    val date: Long,
    val totalItems: PersistentList<InterviewModel>,
    val initialPage: Int,
    val pages: PersistentList<InterviewListPagingState>
)

data class InterviewListPagingState(
    val date: Long,
    val items: PersistentList<InterviewListItemState>
)