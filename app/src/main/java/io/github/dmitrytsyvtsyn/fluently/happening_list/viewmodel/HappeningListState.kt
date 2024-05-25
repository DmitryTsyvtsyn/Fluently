package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewState
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.collections.immutable.PersistentList

data class HappeningListState(
    val date: Long,
    val totalItems: PersistentList<HappeningModel>,
    val currentPage: Int,
    val pages: PersistentList<InterviewListPagingState>
) : ViewState

data class InterviewListPagingState(
    val date: Long,
    val items: PersistentList<HappeningListItemState>
)

sealed interface HappeningListItemState {

    data class Title(val value: String) : HappeningListItemState

    data class Content(
        val model: HappeningModel,
        val status: HappeningTimingStatus
    ) : HappeningListItemState

    data class Timeline(val startDate: Long, val endDate: Long) : HappeningListItemState
}

enum class HappeningTimingStatus {
    PASSED, ACTUAL
}