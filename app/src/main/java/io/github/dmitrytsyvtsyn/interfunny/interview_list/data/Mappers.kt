package io.github.dmitrytsyvtsyn.interfunny.interview_list.data

import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewModel

fun InterviewDatabase.toModel(): InterviewModel =
    InterviewModel(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
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