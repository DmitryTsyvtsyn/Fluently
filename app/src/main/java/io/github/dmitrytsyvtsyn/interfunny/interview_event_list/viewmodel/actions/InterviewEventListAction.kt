package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.actions

sealed interface InterviewEventListAction {
    data object Empty : InterviewEventListAction
    data class Detail(val id: Long = -1) : InterviewEventListAction
    data class ShowDatePicker(val date: Long) : InterviewEventListAction
    data class ShowDropdownMenu(val id: Long, val eventId: Long, val reminderId: Long) : InterviewEventListAction
}