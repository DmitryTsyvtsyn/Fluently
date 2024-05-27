package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class HappeningListViewModel : BaseViewModel<HappeningListEvent, HappeningListState, HappeningListSideEffect>(
    HappeningListState(
        date = System.currentTimeMillis(),
        totalItems = persistentListOf(),
        currentPage = 0,
        pages = persistentListOf(),
    )
) {

    private val repository = HappeningRepository(DI.database.happeningDao(), DI.platformAPI)

    override fun handleEvent(event: HappeningListEvent) {
        when (event) {
            is HappeningListEvent.ChangeDate -> handleEvent(event)
            is HappeningListEvent.ChangeDateByPageIndex -> handleEvent(event)
            is HappeningListEvent.ChangePagesByPageIndex -> handleEvent(event)
            is HappeningListEvent.RemoveHappening -> handleEvent(event)
            is HappeningListEvent.ShowHappeningEditing -> handleEvent(event)
            is HappeningListEvent.ShowHappeningAdding -> handleEvent(event)
            is HappeningListEvent.ShowDatePicker -> handleEvent(event)
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDate) {
        if (viewState.value.date == event.date) return

        setState {
            copy(
                date = event.date,
                pages = totalItems.filteredAndSortedPages(event.date)
            )
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDateByPageIndex) {
        val pageIndex = event.index
        if (pageIndex !in viewState.value.pages.indices) return
        setState {
            copy(
                date = pages[pageIndex].date,
                currentPage = pageIndex
            )
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangePagesByPageIndex) {
        val pageIndex = event.index
        if (pageIndex !in viewState.value.pages.indices) return
        if (pageIndex == 0 || pageIndex == PAGE_SIZE - 1) {
            setState {
                val newDate = pages[pageIndex].date
                copy(
                    date = newDate,
                    currentPage = PAGE_SIZE / 2,
                    pages = totalItems.filteredAndSortedPages(newDate)
                )
            }
        }
    }

    private fun handleEvent(event: HappeningListEvent.RemoveHappening) = viewModelScope.launch {
        repository.delete(event.id, event.eventId, event.reminderId)
        init()
    }

    private fun handleEvent(event: HappeningListEvent.ShowHappeningEditing) {
        setEffect(HappeningListSideEffect.ShowDetail(event.id, viewState.value.date))
    }

    private fun handleEvent(event: HappeningListEvent.ShowHappeningAdding) {
        setEffect(HappeningListSideEffect.ShowDetail(-1, viewState.value.date))
    }

    private fun handleEvent(event: HappeningListEvent.ShowDatePicker) {
        setEffect(HappeningListSideEffect.ShowDatePicker(viewState.value.date))
    }

    fun init() = viewModelScope.launch {
        val happenings = repository.fetch().toPersistentList()

        setState {
            copy(
                totalItems = happenings,
                currentPage = PAGE_SIZE / 2,
                pages = happenings.filteredAndSortedPages(viewState.value.date)
            )
        }
    }

    private fun PersistentList<HappeningModel>.filteredAndSortedPages(date: Long): PersistentList<InterviewListPagingState> {
        val centerIndex = PAGE_SIZE / 2
        val pages = MutableList(PAGE_SIZE) { index ->
            val pageDate = CalendarRepository.plusDays(date, index - centerIndex)
            Triple<Long, LongRange, MutableList<HappeningListItemState>>(
                pageDate,
                CalendarRepository.dateRangeInDays(pageDate, 1),
                mutableListOf()
            )
        }

        var pageIndex = 0
        var itemIndex = 0
        var listItemIndex = 0
        val minimumTimelineInterval = CalendarRepository.minutesInMillis(15)
        val items = sortedBy { it.startDate }
        val nowDate = CalendarRepository.nowDate()
        while (itemIndex < items.size && pageIndex < pages.size) {

            val item = items[itemIndex]
            val (_, dateRange, listItems) = pages[pageIndex]

            if (item.startDate in dateRange || item.endDate in dateRange) {
                if (listItems.isEmpty()) {
                    listItems.add(HappeningListItemState.Title("00:00"))
                }

                if (listItemIndex == 0) {
                    if ((item.startDate - dateRange.first) > minimumTimelineInterval) {
                        listItems.add(HappeningListItemState.Timeline(dateRange.first, item.startDate))
                    }
                } else {
                    if ((item.startDate - items[itemIndex - 1].endDate) > minimumTimelineInterval) {
                        listItems.add(HappeningListItemState.Timeline(items[itemIndex - 1].endDate, item.startDate))
                    }
                }

                listItems.add(HappeningListItemState.Content(item, if (item.endDate < nowDate) HappeningTimingStatus.PASSED else HappeningTimingStatus.ACTUAL))

                val nextPageIndex = pageIndex + 1
                val nextIndex = itemIndex + 1
                when {
                    nextPageIndex < pages.size && item.endDate in pages[nextPageIndex].second -> {
                        if ((dateRange.last - item.endDate) > minimumTimelineInterval) {
                            listItems.add(HappeningListItemState.Timeline(item.endDate, dateRange.last))
                        }

                        listItems.add(HappeningListItemState.Title("24:00"))

                        listItemIndex = 0
                        pageIndex++
                    }
                    nextIndex >= items.size || items[nextIndex].startDate !in dateRange -> {
                        if ((dateRange.last - item.endDate) > minimumTimelineInterval) {
                            listItems.add(HappeningListItemState.Timeline(item.endDate, dateRange.last))
                        }

                        listItems.add(HappeningListItemState.Title("24:00"))

                        listItemIndex = 0
                        pageIndex++
                        itemIndex++
                    }
                    else -> {
                        listItemIndex++
                        itemIndex++
                    }
                }
            } else {
                listItemIndex = 0
                pageIndex++
            }
        }

        return pages.map { (date, _, models) -> InterviewListPagingState(date, models.toPersistentList()) }
            .toPersistentList()
    }

    companion object {
        private const val PAGE_SIZE = 15
    }

}