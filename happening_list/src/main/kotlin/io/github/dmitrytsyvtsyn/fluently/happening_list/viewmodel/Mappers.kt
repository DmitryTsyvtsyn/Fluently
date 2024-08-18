package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.usecases.HappeningFetchPagesUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDateTime

internal fun List<HappeningFetchPagesUseCase.FetchPageUseCaseItems>.toHappeningListPagingState(
    nowDateTime: LocalDateTime
): PersistentList<HappeningListPagingState> {
    val pages = this
    return pages.mapIndexed { index, page ->

        val items = if (page.isEmpty) {
            persistentListOf()
        } else {
            buildList {
                add(HappeningListItemState.Title("00:00"))

                page.items.forEach { pageItem ->
                    when (pageItem) {
                        is HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem -> {
                            add(
                                pageItem.model.toContent(index, nowDateTime, page, pages)
                            )
                        }
                        is HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem -> {
                            add(
                                HappeningListItemState.Timeline(pageItem.period)
                            )
                        }
                    }
                }

                add(HappeningListItemState.Title("24:00"))
            }.toPersistentList()
        }

        HappeningListPagingState(
            dateTime = page.dateTime,
            items = items
        )
    }.toPersistentList()
}

private fun HappeningModel.toContent(
    index: Int,
    nowDateTime: LocalDateTime,
    page: HappeningFetchPagesUseCase.FetchPageUseCaseItems,
    pages: List<HappeningFetchPagesUseCase.FetchPageUseCaseItems>
): HappeningListItemState.Content {
    val happening = this
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

    return HappeningListItemState.Content(
        model = happening,
        timingStatus = timingStatus,
        dayStatus = dayStatus
    )
}