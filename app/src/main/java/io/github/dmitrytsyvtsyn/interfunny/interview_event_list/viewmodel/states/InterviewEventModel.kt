package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states

data class InterviewEventModel(
    val id: Long = -1,
    val eventId: Long = -1,
    val reminderId: Long = -1,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val status: InterviewEventStatus
)