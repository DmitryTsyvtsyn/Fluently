package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states

sealed interface InterviewListItemState {

    data class Title(val value: String) : InterviewListItemState

    data class Content(
        val model: InterviewModel,
        val status: InterviewTimingStatus
    ) : InterviewListItemState

    data class Timeline(val startDate: Long, val endDate: Long) : InterviewListItemState
}