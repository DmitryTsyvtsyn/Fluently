package io.github.dmitrytsyvtsyn.fluently

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.datetime.LocalDateTime

class HappeningRepositoryTestImpl(private val mockHappenings: List<HappeningModel>) : HappeningRepository {

    override suspend fun insert(model: HappeningModel, hasReminder: Boolean) {}
    override suspend fun delete(model: HappeningModel) {}
    override suspend fun fetch(id: IdLong): HappeningModel { error("no needed") }
    override suspend fun fetch(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<HappeningModel> {
        return mockHappenings.filter { it.startDateTime < endDateTime && it.endDateTime > startDateTime }
    }

}