package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.interview_list.CalendarRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.data.InterviewRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.actions.InterviewListAction
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListItemState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListPagingState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewModel
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewTimingStatus
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InterviewListViewModel : ViewModel() {

    private val repository = InterviewRepository(DI.sqliteHelper, DI.platformAPI)

    private val _state: MutableStateFlow<InterviewListState> = MutableStateFlow(
        InterviewListState(
            date = System.currentTimeMillis(),
            totalItems = persistentListOf(),
            initialPage = 0,
            pages = persistentListOf(),
        )
    )
    val state: StateFlow<InterviewListState> = _state

    private val _action = MutableStateFlow<InterviewListAction>(InterviewListAction.Empty)
    val action: StateFlow<InterviewListAction> = _action

    fun init() = viewModelScope.launch {
        val state = _state.value
        val interviews = repository.fetchInterviewEvents().toPersistentList()

        val pages = interviews.filteredAndSortedPages(state.date)
        _state.value = state.copy(
            totalItems = interviews,
            initialPage = pages.size / 2,
            pages = pages
        )
    }

    fun changeDate(newDate: Long) {
        val state = _state.value
        if (newDate == state.date) return

        _state.value = state.copy(
            date = newDate,
            pages = state.totalItems.filteredAndSortedPages(newDate)
        )
    }

    fun resetAction() {
        _action.value = InterviewListAction.Empty
    }

    fun changeDateByPageIndex(page: Int) {
        val state = _state.value
        if (page !in state.pages.indices) return
        _state.value = state.copy(date = state.pages[page].date)
    }

    fun changePagesByPageIndex(page: Int) {
        val state = _state.value
        if (page !in state.pages.indices) return
        if (page == 0 || page == PAGE_SIZE - 1) {

            val newDate = state.pages[page].date
            _state.value = state.copy(
                date = newDate,
                pages = state.totalItems.filteredAndSortedPages(newDate)
            )
        }
    }

    fun showDatePicker() {
        _action.value = InterviewListAction.ShowDatePicker(_state.value.date)
    }

    fun navigateToDetail(id: Long) {
        _action.value = InterviewListAction.Detail(id, _state.value.date)
    }

    fun navigateToAddingInterview() {
        _action.value = InterviewListAction.Detail(-1, _state.value.date)
    }

    fun removeInterview(id: Long, eventId: Long, reminderId: Long) = viewModelScope.launch {
        repository.removeInterviewEvent(id, eventId, reminderId)
        init()
    }

    private fun PersistentList<InterviewModel>.filteredAndSortedPages(date: Long): PersistentList<InterviewListPagingState> {
        val centerIndex = PAGE_SIZE / 2
        val pages = MutableList(PAGE_SIZE) { index ->
            val pageDate = CalendarRepository.plusDays(date, index - centerIndex)
            Triple<Long, LongRange, MutableList<InterviewListItemState>>(
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
                    listItems.add(InterviewListItemState.Title("00:00"))
                }

                if (listItemIndex == 0) {
                    if ((item.startDate - dateRange.first) > minimumTimelineInterval) {
                        listItems.add(InterviewListItemState.Timeline(dateRange.first, item.startDate))
                    }
                } else {
                    if ((item.startDate - items[itemIndex - 1].endDate) > minimumTimelineInterval) {
                        listItems.add(InterviewListItemState.Timeline(items[itemIndex - 1].endDate, item.startDate))
                    }
                }

                listItems.add(InterviewListItemState.Content(item, if (item.endDate < nowDate) InterviewTimingStatus.PASSED else InterviewTimingStatus.ACTUAL))

                val nextPageIndex = pageIndex + 1
                val nextIndex = itemIndex + 1
                when {
                    nextPageIndex < pages.size && item.endDate in pages[nextPageIndex].second -> {
                        if ((dateRange.last - item.endDate) > minimumTimelineInterval) {
                            listItems.add(InterviewListItemState.Timeline(item.endDate, dateRange.last))
                        }

                        listItems.add(InterviewListItemState.Title("24:00"))

                        listItemIndex = 0
                        pageIndex++
                    }
                    nextIndex >= items.size || items[nextIndex].startDate !in dateRange -> {
                        if ((dateRange.last - item.endDate) > minimumTimelineInterval) {
                            listItems.add(InterviewListItemState.Timeline(item.endDate, dateRange.last))
                        }

                        listItems.add(InterviewListItemState.Title("24:00"))

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