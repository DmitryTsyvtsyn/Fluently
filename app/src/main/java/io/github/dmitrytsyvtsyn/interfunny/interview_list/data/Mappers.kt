package io.github.dmitrytsyvtsyn.interfunny.interview_list.data

import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewModel
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewEventStatus

fun InterviewDatabase.toModel(nowDate: Long): InterviewModel =
    InterviewModel(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        status = if (nowDate > endDate) InterviewEventStatus.PASSED else InterviewEventStatus.ACTUAL
    )

fun InterviewModel.toDatabase(): InterviewDatabase =
    InterviewDatabase(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )