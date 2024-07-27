package io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases

import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HappeningInsertUseCase(private val repository: HappeningRepository) {

    suspend fun execute(model: HappeningModel, hasReminder: Boolean = false) =
        withContext(Dispatchers.Default) {
            repository.insert(model, hasReminder)
        }

}