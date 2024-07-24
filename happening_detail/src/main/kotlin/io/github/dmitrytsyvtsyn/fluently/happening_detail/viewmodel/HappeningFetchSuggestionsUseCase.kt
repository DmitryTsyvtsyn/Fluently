package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.datetime.compareTo
import io.github.dmitrytsyvtsyn.fluently.core.datetime.minus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

class HappeningFetchSuggestionsUseCase(private val repository: HappeningRepository) {

    // TODO(reformat this use case)
    suspend fun execute(
        happening: HappeningModel,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ) = withContext(Dispatchers.Default) {

        val alreadyScheduledEvents = mutableListOf<HappeningModel>()
        val suggestionRanges = mutableListOf<LongRange>()
        var currentDate = startDateTime

        val minimumSuggestionRangesCount = 2

        val minimumInterval = endDateTime.minus(startDateTime).plus(DateTimePeriod(minutes = 10))

        val currentHappeningId = happening.id
        val dateRange = startDateTime..endDateTime

        val happeningsByStartDateEndDateRange = repository.fetch(startDateTime, endDateTime)
        val happeningsByStartDateEndDateRangeSize = happeningsByStartDateEndDateRange.size
        var index = 0
        while (index < happeningsByStartDateEndDateRangeSize) {
            val happening = happeningsByStartDateEndDateRange[index]

            val isCurrentHappening = happening.id == currentHappeningId
            if (isCurrentHappening) {
                index++
                continue
            }

            if (happening.startDateTime in dateRange || happening.endDateTime in dateRange) {
                alreadyScheduledEvents.add(happening)
            }

            if (currentDate < happening.startDateTime && suggestionRanges.size < minimumSuggestionRangesCount) {
                val differenceBetweenEndAndStartDates = happening.startDateTime.minus(currentDate)
                if (differenceBetweenEndAndStartDates > minimumInterval) {
                    val startSuggestionDate = currentDate.plus(1, DateTimeUnit.MINUTE)
                    //suggestionRanges.add(startSuggestionDate..startSuggestionDate.plus(minimumInterval))
                }
            }
            currentDate = happening.endDateTime

            index++
        }

        if (alreadyScheduledEvents.isEmpty()) {
            return@withContext FetchSuggestionsUseCaseResult.NoScheduledEvents
        }

        //while (suggestionRanges.size < 2) {
            //suggestionRanges.add(currentDate..currentDate + minimumInterval)
            //currentDate += 2 * minimumInterval
        //}

        FetchSuggestionsUseCaseResult.SuggestionRanges(suggestionRanges)
    }

    sealed interface FetchSuggestionsUseCaseResult {
        data object NoScheduledEvents : FetchSuggestionsUseCaseResult
        class SuggestionRanges(val ranges: List<LongRange>) : FetchSuggestionsUseCaseResult
    }

}