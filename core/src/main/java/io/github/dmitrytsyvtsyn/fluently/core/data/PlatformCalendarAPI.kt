package io.github.dmitrytsyvtsyn.fluently.core.data

interface PlatformCalendarAPI {

    suspend fun insertEventWithReminder(
        title: String,
        startDate: Long,
        endDate: Long
    ): InsertEventResult

    suspend fun updateEventWithReminder(
        eventId: IdLong,
        reminderId: IdLong,
        title: String,
        startDate: Long,
        endDate: Long
    ): Boolean

    suspend fun removeEventWithReminder(eventId: IdLong, reminderId: IdLong): Boolean

}

class InsertEventResult(val eventId: IdLong, val reminderId: IdLong) {
    operator fun component1() = eventId
    operator fun component2() = reminderId
}

