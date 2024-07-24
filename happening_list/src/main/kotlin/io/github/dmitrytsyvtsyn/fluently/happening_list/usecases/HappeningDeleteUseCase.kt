package io.github.dmitrytsyvtsyn.fluently.happening_list.usecases

import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HappeningDeleteUseCase(private val repository: HappeningRepository) {

    suspend fun execute(model: HappeningModel) = withContext(Dispatchers.Default) {
        repository.delete(model)
    }

}