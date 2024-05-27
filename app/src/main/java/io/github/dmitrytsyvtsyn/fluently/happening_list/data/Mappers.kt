package io.github.dmitrytsyvtsyn.fluently.happening_list.data

import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel

fun HappeningTable.toModel(): HappeningModel =
    HappeningModel(
        id = id,
        eventId = calendarEventId,
        reminderId = calendarReminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )

fun HappeningModel.toDatabase(): HappeningTable =
    HappeningTable(
        id = id,
        calendarEventId = eventId,
        calendarReminderId = reminderId,
        title = title,
        startDate = startDate,
        endDate = endDate
    )