package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchSuggestionsUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDateTime

internal fun HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.toSuggestionRanges(
    happening: HappeningModel
): PersistentList<ClosedRange<LocalDateTime>> {
    val result = this

    if (result is HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.Suggestions) {
        val plannedHappenings = result.plannedHappenings
        if (plannedHappenings.size > 1 || plannedHappenings.first() != happening) {
            return result.ranges.toPersistentList()
        }
    }

    return persistentListOf()
}