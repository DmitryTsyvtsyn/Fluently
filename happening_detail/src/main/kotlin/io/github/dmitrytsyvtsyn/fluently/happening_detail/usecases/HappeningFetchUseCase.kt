package io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HappeningFetchUseCase(private val repository: HappeningRepository) {

    suspend fun execute(id: IdLong) = withContext(Dispatchers.Default) {
        repository.fetch(id)
    }

}