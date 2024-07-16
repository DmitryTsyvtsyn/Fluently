package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.toIdLong
import java.util.UUID

fun HappeningTable.toModel(): HappeningModel =
    HappeningModel(
        id = id.toIdLong(),
        eventId = calendarEventId.toIdLong(),
        reminderId = calendarReminderId.toIdLong(),
        title = title,
        startDate = startDate,
        endDate = endDate
    )

fun HappeningModel.toDatabase(): HappeningTable =
    if (id.isNotEmpty) {
        HappeningTable(
            id = id.value,
            calendarEventId = eventId.value,
            calendarReminderId = reminderId.value,
            title = title,
            startDate = startDate,
            endDate = endDate
        )
    } else {
        val uuid = UUID.randomUUID()
        val id = uuid.mostSignificantBits
        val limitedId = id.and(Long.MAX_VALUE)
        HappeningTable(
            id = limitedId,
            calendarEventId = eventId.value,
            calendarReminderId = reminderId.value,
            title = title,
            startDate = startDate,
            endDate = endDate
        )
    }