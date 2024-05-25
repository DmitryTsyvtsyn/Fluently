package io.github.dmitrytsyvtsyn.fluently.happening_list.data

import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel

fun InterviewDatabase.toModel(): HappeningModel =
    HappeningModel(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )

fun HappeningModel.toDatabase(): InterviewDatabase =
    InterviewDatabase(
        id = id,
        eventId = eventId,
        reminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )