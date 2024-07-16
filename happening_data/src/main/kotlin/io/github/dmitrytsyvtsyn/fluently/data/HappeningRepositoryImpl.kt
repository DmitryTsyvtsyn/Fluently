package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class HappeningRepositoryImpl(
    private val database: HappeningDao,
    private val calendarAPI: PlatformCalendarAPI
) : HappeningRepository {

    override suspend fun insert(model: HappeningModel, hasReminder: Boolean) =
        withContext(Dispatchers.Default) {
            val happeningId = model.id
            val updatedHappening = when {
                happeningId.isEmpty && hasReminder -> {
                    val (eventId, reminderId) = calendarAPI.insertEventWithReminder(
                        model.title,
                        model.startDate,
                        model.endDate
                    )
                    model.copy(eventId = eventId, reminderId = reminderId)
                }
                happeningId.isNotEmpty && hasReminder -> {
                    calendarAPI.updateEventWithReminder(
                        model.eventId,
                        model.reminderId,
                        model.title,
                        model.startDate,
                        model.endDate
                    )
                    model
                }
                else -> {
                    model
                }
            }

            database.insert(updatedHappening.toDatabase())
        }

    override suspend fun delete(model: HappeningModel) =
        withContext(Dispatchers.Default) {
            calendarAPI.removeEventWithReminder(model.eventId, model.reminderId)
            database.delete(model.id.value)
        }

    override suspend fun fetch(id: IdLong) = withContext(Dispatchers.Default) {
        database.fetch(id.value).toModel()
    }

    override suspend fun fetch(startDate: Long, endDate: Long) = withContext(Dispatchers.Default) {
        database.fetch(startDate, endDate).map { it.toModel() }
    }

}