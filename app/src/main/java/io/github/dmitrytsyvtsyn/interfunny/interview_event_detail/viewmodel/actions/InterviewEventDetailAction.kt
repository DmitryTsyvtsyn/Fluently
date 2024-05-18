package io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.actions

sealed interface InterviewEventDetailAction {
    data object Empty : InterviewEventDetailAction
    data class DatePicker(val date: Long) : InterviewEventDetailAction
    data class TimePicker(val startHours: Int, val startMinutes: Int, val endHours: Int, val endMinutes: Int) : InterviewEventDetailAction
    data object Back : InterviewEventDetailAction
}