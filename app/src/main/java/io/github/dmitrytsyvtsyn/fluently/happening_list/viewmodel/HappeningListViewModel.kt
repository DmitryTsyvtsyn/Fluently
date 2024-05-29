package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.TimeFactorForToday
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HappeningListViewModel : BaseViewModel<HappeningListEvent, HappeningListState, HappeningListSideEffect>(
    HappeningListState(
        nowDate = CalendarRepository.nowDate(),
        currentDate = CalendarRepository.nowDate(),
        currentPage = 0,
        pages = persistentListOf()
    )
) {

    private val repository = HappeningRepository(DI.database.happeningDao(), DI.platformAPI)

    private var cachedInitJob: Job? = null

    override fun handleEvent(event: HappeningListEvent) {
        when (event) {
            is HappeningListEvent.FetchHappenings -> handleEvent(event)
            is HappeningListEvent.ChangeDate -> handleEvent(event)
            is HappeningListEvent.ChangeDateByPageIndex -> handleEvent(event)
            is HappeningListEvent.ChangePagesByPageIndex -> handleEvent(event)
            is HappeningListEvent.RemoveHappening -> handleEvent(event)
            is HappeningListEvent.ShowCalendarEvent -> handleEvent(event)
            is HappeningListEvent.ShowHappeningEditing -> handleEvent(event)
            is HappeningListEvent.ShowHappeningAdding -> handleEvent(event)
            is HappeningListEvent.ShowDatePicker -> handleEvent(event)
            is HappeningListEvent.SubscribeTimeUpdates -> handleEvent(event)
            is HappeningListEvent.UnsubscribeTimeUpdates -> handleEvent(event)
        }
    }

    private fun handleEvent(event: HappeningListEvent.FetchHappenings) {
        cachedInitJob?.cancel()
        cachedInitJob = viewModelScope.launch {
            val initialDate = CalendarRepository.nowDate()
            val pages = calculatePages(event.date, initialDate)
            setState {
                copy(
                    nowDate = initialDate,
                    currentPage = PAGE_SIZE / 2,
                    currentDate = currentDate,
                    pages = pages
                )
            }

            val currentDate = pages[pages.size / 2].date
            var remainingMillisForNextMinute = CalendarRepository.calculateMillisForNextMinute(initialDate)
            while (true) {
                delay(remainingMillisForNextMinute)

                val nowDate = CalendarRepository.nowDate()
                val updatedPages = calculatePages(currentDate, nowDate)
                setState {
                    copy(
                        nowDate = nowDate,
                        pages = updatedPages
                    )
                }

                remainingMillisForNextMinute = CalendarRepository.calculateMillisForNextMinute(nowDate)
            }
        }
    }

    private suspend fun calculatePages(currentDate: Long, nowDate: Long): PersistentList<HappeningListPagingState> {
        val (startDate, endDate) = currentDate.calculatePageRange()
        val happenings = repository.fetch(startDate, endDate).toPersistentList()

        return happenings.filteredAndSortedPages(startDate, endDate, nowDate)
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDate) {
        if (CalendarRepository.compareDates(viewState.value.currentDate, event.date)) return

        handleEvent(HappeningListEvent.FetchHappenings(event.date))
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDateByPageIndex) {
        val pageIndex = event.index
        if (pageIndex !in viewState.value.pages.indices) return
        setState {
            copy(
                currentDate = pages[pageIndex].date,
                currentPage = pageIndex
            )
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangePagesByPageIndex) {
        val pageIndex = event.index
        if (pageIndex !in viewState.value.pages.indices) return
        if (pageIndex == FINAL_START_PAGE || pageIndex == FINAL_END_PAGE) {
            val date = viewState.value.pages[pageIndex].date
            handleEvent(HappeningListEvent.FetchHappenings(date))
        }
    }

    private fun handleEvent(event: HappeningListEvent.RemoveHappening) = viewModelScope.launch {
        repository.delete(event.id, event.eventId, event.reminderId)
        handleEvent(HappeningListEvent.FetchHappenings(viewState.value.currentDate))
    }

    private fun handleEvent(event: HappeningListEvent.ShowCalendarEvent) {
        setEffect(HappeningListSideEffect.ShowCalendarEvent(event.id))
    }

    private fun handleEvent(event: HappeningListEvent.ShowHappeningEditing) {
        setEffect(HappeningListSideEffect.ShowDetail(event.id, viewState.value.currentDate))
    }

    private fun handleEvent(event: HappeningListEvent.ShowHappeningAdding) {
        setEffect(HappeningListSideEffect.ShowDetail(-1, viewState.value.currentDate))
    }

    private fun handleEvent(event: HappeningListEvent.ShowDatePicker) {
        setEffect(HappeningListSideEffect.ShowDatePicker(viewState.value.currentDate))
    }

    private fun handleEvent(event: HappeningListEvent.SubscribeTimeUpdates) {
        if (cachedInitJob == null) {
            handleEvent(HappeningListEvent.FetchHappenings(CalendarRepository.nowDate()))
        }
    }

    private fun handleEvent(event: HappeningListEvent.UnsubscribeTimeUpdates) {
        cachedInitJob?.cancel()
    }

    private fun Long.calculatePageRange(): Pair<Long, Long> {
        val date = this
        val daysOffset = PAGE_SIZE / 2
        val startDate = CalendarRepository.minusDays(date, daysOffset)
        val endDate = CalendarRepository.plusDays(date, daysOffset)
        return startDate to endDate
    }

    private fun PersistentList<HappeningModel>.filteredAndSortedPages(startDate: Long, endDate: Long, nowDate: Long): PersistentList<HappeningListPagingState> {
        val pages = mutableListOf<Triple<Long, LongRange, MutableList<HappeningListItemState>>>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            pages.add(
                Triple(
                    currentDate,
                    CalendarRepository.dateRangeInDays(currentDate),
                    mutableListOf()
                )
            )
            currentDate = CalendarRepository.plusDays(currentDate, 1)
        }

        var pageIndex = 0
        var itemIndex = 0
        var listItemIndex = 0
        val minimumTimelineInterval = CalendarRepository.minutesInMillis(15)
        while (itemIndex < size && pageIndex < pages.size) {

            val item = this[itemIndex]
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
                    if ((item.startDate - this[itemIndex - 1].endDate) > minimumTimelineInterval) {
                        listItems.add(HappeningListItemState.Timeline(this[itemIndex - 1].endDate, item.startDate))
                    }
                }

                val timingStatus = if (item.endDate < nowDate) HappeningTimingStatus.PASSED else HappeningTimingStatus.ACTUAL
                val dayStatus = when {
                    item.startDate in dateRange && item.endDate > dateRange.last -> HappeningDayStatus.TOMORROW
                    item.endDate in dateRange && item.startDate < dateRange.first -> HappeningDayStatus.YESTERDAY
                    else -> HappeningDayStatus.ONLY_TODAY
                }
                listItems.add(
                    HappeningListItemState.Content(
                        model = item,
                        timingStatus = timingStatus,
                        dayStatus = dayStatus
                    )
                )

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
                    nextIndex >= size || this[nextIndex].startDate !in dateRange -> {
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

        val currentTime = CalendarRepository.timeFromDate(nowDate)
        return pages.map { (date, _, models) ->
            val isToday = CalendarRepository.compareDates(date, nowDate)
            HappeningListPagingState(
                timeFactorForToday = if (isToday) TimeFactorForToday.from(currentTime) else TimeFactorForToday.Invalid,
                date = date,
                items = models.toPersistentList()
            )
        }.toPersistentList()
    }

    companion object {
        private const val FINAL_START_PAGE = 0
        private const val PAGE_SIZE = 15
        private const val FINAL_END_PAGE = PAGE_SIZE - 1
    }

}