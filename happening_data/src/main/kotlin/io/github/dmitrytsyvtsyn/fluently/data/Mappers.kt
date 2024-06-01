package io.github.dmitrytsyvtsyn.fluently.data

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