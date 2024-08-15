package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.happening_list.usecases.HappeningFetchPagesUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDateTime

internal fun List<HappeningFetchPagesUseCase.FetchPageUseCaseItems>.toHappeningListPagingState(
    nowDateTime: LocalDateTime
): PersistentList<HappeningListPagingState> {
    val pages = this
    return pages.mapIndexed { index, page ->

        val formattedPageItems = mutableListOf<HappeningListItemState>()
        formattedPageItems.add(HappeningListItemState.Title("00:00"))

        val pageItems = page.items
        pageItems.forEach { pageItem ->
            when (pageItem) {
                is HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem -> {
                    val happening = pageItem.model
                    val timingStatus = if (happening.endDateTime >= nowDateTime) {
                        HappeningRunningStatus.ACTUAL
                    } else {
                        HappeningRunningStatus.PASSED
                    }

                    val prevPage = pages.getOrNull(index - 1)
                    val nextPage = pages.getOrNull(index + 1)
                    val currentPageDate = page.dateTime.date
                    val happeningStartDate = happening.startDateTime.date
                    val happeningEndDate = happening.endDateTime.date
                    val dayStatus = when {
                        happeningStartDate == currentPageDate && happeningEndDate == nextPage?.dateTime?.date -> {
                            HappeningDayStatus.TODAY_AND_TOMORROW
                        }
                        happeningEndDate == currentPageDate && happeningStartDate == prevPage?.dateTime?.date -> {
                            HappeningDayStatus.TODAY_AND_YESTERDAY
                        }
                        else -> {
                            HappeningDayStatus.ONLY_TODAY
                        }
                    }

                    formattedPageItems.add(
                        HappeningListItemState.Content(
                            model = happening,
                            timingStatus = timingStatus,
                            dayStatus = dayStatus
                        )
                    )
                }
                is HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem -> {
                    formattedPageItems.add(
                        HappeningListItemState.Timeline(pageItem.period)
                    )
                }
            }
        }

        formattedPageItems.add(HappeningListItemState.Title("24:00"))

        HappeningListPagingState(
            dateTime = page.dateTime,
            items = formattedPageItems.toPersistentList()
        )
    }.toPersistentList()
}