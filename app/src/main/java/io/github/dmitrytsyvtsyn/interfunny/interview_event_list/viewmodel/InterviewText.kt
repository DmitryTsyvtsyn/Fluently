package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel

sealed interface InterviewText {
    data class Resource(val id: Int) : InterviewText
    data class Text(val value: String) : InterviewText
}