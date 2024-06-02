package io.github.dmitrytsyvtsyn.fluently.data

interface HappeningRepository {
    suspend fun insert(model: HappeningModel, hasReminder: Boolean = false)
    suspend fun delete(id: Long, eventId: Long, reminderId: Long)
    suspend fun fetch(id: Long): HappeningModel
    suspend fun fetch(startDate: Long, endDate: Long): List<HappeningModel>
}

