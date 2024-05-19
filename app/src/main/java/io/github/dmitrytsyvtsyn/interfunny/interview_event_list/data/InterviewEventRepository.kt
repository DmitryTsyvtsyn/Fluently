package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data

import io.github.dmitrytsyvtsyn.interfunny.core.data.InterviewSqliteHelper
import io.github.dmitrytsyvtsyn.interfunny.core.data.PlatformCalendarAPI
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InterviewEventRepository(
    private val database: InterviewSqliteHelper,
    private val calendarAPI: PlatformCalendarAPI
) {

    suspend fun addInterviewEvent(model: InterviewEventModel, hasReminder: Boolean = false) = withContext(Dispatchers.Default) {
        if (model.id >= 0) {
            val reminderId = calendarAPI.updateEvent(model.eventId, model.reminderId, model.title, model.startDate, model.endDate, hasReminder)
            database.update(model.copy(reminderId = reminderId).toDatabase())
        } else {
            val (eventId, reminderId) = calendarAPI.insertEvent(model.title, model.startDate, model.endDate, hasReminder)
            database.insert(model.copy(eventId = eventId, reminderId = reminderId).toDatabase())
        }
    }

    suspend fun removeInterviewEvent(id: Long, eventId: Long, reminderId: Long) = withContext(Dispatchers.Default) {
        calendarAPI.removeEvent(eventId, reminderId)
        database.delete(id)
    }

    suspend fun fetchInterviewEvent(
        id: Long,
        nowDate: Long
    ) = withContext(Dispatchers.Default) {
        database.fetch(id).toModel(nowDate)
    }

    suspend fun fetchInterviewEvents(
        nowDate: Long
    ) = withContext(Dispatchers.Default) {
        database.fetch().map { it.toModel(nowDate) }
    }

}