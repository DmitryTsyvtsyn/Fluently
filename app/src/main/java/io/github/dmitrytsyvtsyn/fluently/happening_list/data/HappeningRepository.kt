package io.github.dmitrytsyvtsyn.fluently.happening_list.data

import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HappeningRepository(
    private val database: HappeningDao,
    private val calendarAPI: PlatformCalendarAPI
) {

    suspend fun insert(model: HappeningModel, hasReminder: Boolean = false) = withContext(Dispatchers.Default) {
        if (model.id > 0) {
            val reminderId = calendarAPI.updateEvent(model.eventId, model.reminderId, model.title, model.startDate, model.endDate, hasReminder)
            database.insert(model.copy(reminderId = reminderId).toDatabase())
            //database.update(model.copy(reminderId = reminderId).toDatabase())
        } else {
            val (eventId, reminderId) = calendarAPI.insertEvent(model.title, model.startDate, model.endDate, hasReminder)
            database.insert(model.copy(eventId = eventId, reminderId = reminderId).toDatabase())
        }
    }

    suspend fun delete(id: Long, eventId: Long, reminderId: Long) = withContext(Dispatchers.Default) {
        calendarAPI.removeEvent(eventId, reminderId)
        database.delete(id)
    }

    suspend fun fetch(id: Long) = withContext(Dispatchers.Default) {
        database.fetch(id).toModel()
    }

    suspend fun fetch() = withContext(Dispatchers.Default) {
        database.fetch().map { it.toModel() }
    }

}