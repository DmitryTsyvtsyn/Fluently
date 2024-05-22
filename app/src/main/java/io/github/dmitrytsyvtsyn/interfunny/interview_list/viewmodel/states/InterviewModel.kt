package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states

data class InterviewModel(
    val id: Long = -1,
    val eventId: Long = -1,
    val reminderId: Long = -1,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val status: InterviewEventStatus
)