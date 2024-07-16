package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong

interface HappeningRepository {
    suspend fun insert(model: HappeningModel, hasReminder: Boolean = false)
    suspend fun delete(model: HappeningModel)
    suspend fun fetch(id: IdLong): HappeningModel
    suspend fun fetch(startDate: Long, endDate: Long): List<HappeningModel>
}

