package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.actions

sealed interface InterviewListAction {
    data object Empty : InterviewListAction
    data class Detail(val id: Long = -1, val date: Long = System.currentTimeMillis()) : InterviewListAction
    data class ShowDatePicker(val date: Long) : InterviewListAction
    data class ShowDropdownMenu(val id: Long, val eventId: Long, val reminderId: Long) : InterviewListAction
}