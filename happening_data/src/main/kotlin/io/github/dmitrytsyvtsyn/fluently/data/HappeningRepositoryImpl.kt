package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class HappeningRepositoryImpl(
    private val database: HappeningDao,
    private val calendarAPI: PlatformCalendarAPI
) : HappeningRepository {

    override suspend fun insert(model: HappeningModel, hasReminder: Boolean) =
        withContext(Dispatchers.Default) {
            if (model.id > 0) {
                val reminderId = calendarAPI.updateEvent(
                    model.eventId,
                    model.reminderId,
                    model.title,
                    model.startDate,
                    model.endDate,
                    hasReminder
                )
                database.insert(model.copy(reminderId = reminderId).toDatabase())
            } else {
                val (eventId, reminderId) = calendarAPI.insertEvent(
                    model.title,
                    model.startDate,
                    model.endDate,
                    hasReminder
                )
                database.insert(model.copy(eventId = eventId, reminderId = reminderId).toDatabase())
            }
        }

    override suspend fun delete(id: Long, eventId: Long, reminderId: Long) =
        withContext(Dispatchers.Default) {
            calendarAPI.removeEvent(eventId, reminderId)
            database.delete(id)
        }

    override suspend fun fetch(id: Long) = withContext(Dispatchers.Default) {
        database.fetch(id).toModel()
    }

    override suspend fun fetch(startDate: Long, endDate: Long) = withContext(Dispatchers.Default) {
        database.fetch(startDate, endDate).map { it.toModel() }
    }

}