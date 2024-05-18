package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data

import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventStatus

fun InterviewEventDatabase.toModel(nowDate: Long): InterviewEventModel =
    InterviewEventModel(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        status = if (nowDate > endDate) InterviewEventStatus.PASSED else InterviewEventStatus.ACTUAL
    )

fun InterviewEventModel.toDatabase(): InterviewEventDatabase =
    InterviewEventDatabase(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )