package io.github.dmitrytsyvtsyn.fluently.core.data

interface PlatformCalendarAPI {
    suspend fun insertEvent(
        title: String,
        startDate: Long,
        endDate: Long,
        hasReminder: Boolean = false
    ): Pair<Long, Long>
    suspend fun updateEvent(
        eventId: Long,
        reminderId: Long,
        title: String,
        startDate: Long,
        endDate: Long,
        hasReminder: Boolean = false
    ): Long
    suspend fun removeEvent(eventId: Long, reminderId: Long): Boolean

}

