package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.data.database.HappeningDao
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import kotlinx.datetime.LocalDateTime

internal class HappeningRepositoryImpl(
    private val database: HappeningDao,
    private val calendarAPI: PlatformCalendarAPI
) : HappeningRepository {

    override suspend fun insert(model: HappeningModel, hasReminder: Boolean) {
        val happeningEventId = model.eventId
        val updatedHappening = when {
            happeningEventId.isEmpty && hasReminder -> {
                val (eventId, reminderId) = calendarAPI.insertEventWithReminder(
                    model.title,
                    model.startDateTime.toEpochMillis(),
                    model.endDateTime.toEpochMillis()
                )
                model.copy(eventId = eventId, reminderId = reminderId)
            }
            happeningEventId.isNotEmpty && hasReminder -> {
                calendarAPI.updateEventWithReminder(
                    happeningEventId,
                    model.title,
                    model.startDateTime.toEpochMillis(),
                    model.endDateTime.toEpochMillis()
                )
                model
            }
            else -> {
                calendarAPI.removeEventWithReminder(model.eventId, model.reminderId)
                model.copy(eventId = IdLong.Empty, reminderId = IdLong.Empty)
            }
        }

        database.insert(updatedHappening.toDatabase())
    }

    override suspend fun delete(model: HappeningModel) {
        calendarAPI.removeEventWithReminder(model.eventId, model.reminderId)
        database.delete(model.id.value)
    }

    override suspend fun fetch(id: IdLong): HappeningModel {
        return database.fetch(id.value).toModel()
    }

    override suspend fun fetch(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<HappeningModel> {
        return database.fetch(startDateTime.toEpochMillis(), endDateTime.toEpochMillis()).map { it.toModel() }
    }

}