package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import kotlinx.datetime.LocalDateTime

interface HappeningRepository {
    suspend fun insert(model: HappeningModel, hasReminder: Boolean = false)
    suspend fun delete(model: HappeningModel)
    suspend fun fetch(id: IdLong): HappeningModel
    suspend fun fetch(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<HappeningModel>
}

