package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states

import androidx.compose.runtime.Stable

@Stable
sealed interface InterviewListItemState {
    data class Title(val value: String) : InterviewListItemState
    data class Content(val model: InterviewModel) : InterviewListItemState
    data class Timeline(val startDate: Long, val endDate: Long) : InterviewListItemState
}