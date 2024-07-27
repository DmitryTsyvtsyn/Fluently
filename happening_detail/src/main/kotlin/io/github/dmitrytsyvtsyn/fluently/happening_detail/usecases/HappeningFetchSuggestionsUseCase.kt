package io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases

import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime

class HappeningFetchSuggestionsUseCase(private val repository: HappeningRepository) {

    suspend fun execute(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ) = withContext(Dispatchers.Default) {
        var happenings = repository.fetch(startDateTime, endDateTime)
        if (happenings.isEmpty()) {
            return@withContext FetchSuggestionsUseCaseResult.NoSuggestions
        }

        var currentStartDateTime = startDateTime.plus(1, DateTimeUnit.DAY)
        var currentEndDateTime = endDateTime.plus(1, DateTimeUnit.DAY)
        val plannedHappenings = happenings
        val ranges = mutableListOf<ClosedRange<LocalDateTime>>()
        while (ranges.size < 2) {
            val rangeStartDateTime = currentStartDateTime
            val rangeEndDateTime = currentEndDateTime.plus(1, DateTimeUnit.WEEK)
            happenings = repository.fetch(rangeStartDateTime, rangeEndDateTime)

            while (currentStartDateTime.date <= rangeEndDateTime.date) {
                val range = currentStartDateTime..currentEndDateTime

                var happeningsIndex = 0
                var happeningsInRange = false
                val happeningsSize = happenings.size
                while (happeningsIndex < happeningsSize) {
                    val happening = happenings[happeningsIndex]

                    if (happening.startDateTime < currentEndDateTime && happening.endDateTime > currentStartDateTime) {
                        happeningsInRange = true
                        break
                    }

                    happeningsIndex++
                }

                if (!happeningsInRange) {
                    ranges.add(range)
                }

                currentStartDateTime = currentStartDateTime.plus(1, DateTimeUnit.DAY)
                currentEndDateTime = currentEndDateTime.plus(1, DateTimeUnit.DAY)
            }

        }

        FetchSuggestionsUseCaseResult.Suggestions(
            plannedHappenings = plannedHappenings,
            ranges = ranges
        )
    }

    sealed interface FetchSuggestionsUseCaseResult {
        data object NoSuggestions : FetchSuggestionsUseCaseResult
        data class Suggestions(
            val plannedHappenings: List<HappeningModel>,
            val ranges: List<ClosedRange<LocalDateTime>>
        ) : FetchSuggestionsUseCaseResult
    }

}