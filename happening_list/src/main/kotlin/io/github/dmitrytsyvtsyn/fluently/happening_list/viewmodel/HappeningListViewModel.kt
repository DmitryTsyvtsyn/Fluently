package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.coroutines.update
import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.core.datetime.minus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.usecases.HappeningDeleteUseCase
import io.github.dmitrytsyvtsyn.fluently.happening_list.usecases.HappeningFetchPagesUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime

internal class HappeningListViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(
        HappeningListState(
            nowDateTime = DateTimeExtensions.nowDateTime(),
            currentDateTime = DateTimeExtensions.nowDateTime()
        )
    )
    val viewState: StateFlow<HappeningListState> = _viewState

    private val _effect = MutableSharedFlow<HappeningListSideEffect>()
    val effect: SharedFlow<HappeningListSideEffect> = _effect.asSharedFlow()

    private val diComponent = object {
        private val repository = DI.get<HappeningRepository>()

        val fetchPagesUseCase = HappeningFetchPagesUseCase(repository)
        val deleteUseCase = HappeningDeleteUseCase(repository)
    }

    private var cachedInitJob: Job? = null

    fun handleEvent(event: HappeningListEvent) {
        when (event) {
            is HappeningListEvent.FetchHappenings -> handleEvent(event)
            is HappeningListEvent.ChangeDate -> handleEvent(event)
            is HappeningListEvent.ChangeDateByPageIndex -> handleEvent(event)
            is HappeningListEvent.ChangePagesByPageIndex -> handleEvent(event)
            is HappeningListEvent.RemoveHappening -> handleEvent(event)
            is HappeningListEvent.ShowCalendar -> handleEvent(event)
            is HappeningListEvent.EditHappening -> handleEvent(event)
            is HappeningListEvent.ShowHappeningAdding -> handleEvent(event)
            is HappeningListEvent.ShowDatePicker -> handleEvent(event)
            is HappeningListEvent.SubscribeTimeUpdates -> handleEvent(event)
            is HappeningListEvent.UnsubscribeTimeUpdates -> handleEvent(event)
        }
    }

    private fun handleEvent(event: HappeningListEvent.FetchHappenings) {
        cachedInitJob?.cancel()
        cachedInitJob = viewModelScope.launch {
            val initialDate = DateTimeExtensions.nowDateTime()
            val pages = calculatePages(event.date, initialDate)
            _viewState.update {
                copy(
                    nowDateTime = initialDate,
                    currentPage = PAGE_SIZE / 2,
                    pages = pages
                )
            }

            val currentDate = pages[pages.size / 2].dateTime

            var remainingMillisForNextMinute = initialDate.remainingMillisForNextMinute()
            while (true) {
                delay(remainingMillisForNextMinute)

                val nowDateTime = DateTimeExtensions.nowDateTime()
                val updatedPages = calculatePages(currentDate, nowDateTime)
                _viewState.update {
                    copy(
                        nowDateTime = nowDateTime,
                        pages = updatedPages
                    )
                }

                remainingMillisForNextMinute = nowDateTime.remainingMillisForNextMinute()
            }
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDate) {
        if (viewState.value.currentDateTime == event.date) return

        handleEvent(HappeningListEvent.FetchHappenings(event.date))
    }

    private fun handleEvent(event: HappeningListEvent.ChangeDateByPageIndex) {
        val pageIndex = event.index
        if (pageIndex !in viewState.value.pages.indices) return
        _viewState.update {
            copy(
                currentDateTime = pages[pageIndex].dateTime,
                currentPage = pageIndex
            )
        }
    }

    private fun handleEvent(event: HappeningListEvent.ChangePagesByPageIndex) {
        val pageIndex = event.index
        val currentState = _viewState.value
        if (pageIndex !in currentState.pages.indices) return
        if (pageIndex == MIN_PAGE || pageIndex == MAX_PAGE) {
            val date = currentState.pages[pageIndex].dateTime
            handleEvent(HappeningListEvent.FetchHappenings(date))
        }
    }

    private fun handleEvent(event: HappeningListEvent.RemoveHappening) = viewModelScope.launch {
        diComponent.deleteUseCase.execute(event.happening)
        handleEvent(HappeningListEvent.FetchHappenings(_viewState.value.currentDateTime))
    }

    private fun handleEvent(event: HappeningListEvent.ShowCalendar) = viewModelScope.launch {
        _effect.emit(HappeningListSideEffect.ShowCalendar(event.happening.id))
    }

    private fun handleEvent(event: HappeningListEvent.EditHappening) = viewModelScope.launch {
        _effect.emit(HappeningListSideEffect.ShowDetail(event.happening.id, _viewState.value.currentDateTime))
    }

    private fun handleEvent(event: HappeningListEvent.ShowHappeningAdding) = viewModelScope.launch {
        _effect.emit(HappeningListSideEffect.ShowDetail(IdLong.Empty, _viewState.value.currentDateTime))
    }

    private fun handleEvent(event: HappeningListEvent.ShowDatePicker) = viewModelScope.launch {
        _effect.emit(HappeningListSideEffect.ShowDatePicker(_viewState.value.currentDateTime))
    }

    private fun handleEvent(event: HappeningListEvent.SubscribeTimeUpdates) {
        if (cachedInitJob == null) {
            handleEvent(HappeningListEvent.FetchHappenings(DateTimeExtensions.nowDateTime()))
        }
    }

    private fun handleEvent(event: HappeningListEvent.UnsubscribeTimeUpdates) {
        cachedInitJob?.cancel()
    }

    private fun LocalDateTime.remainingMillisForNextMinute(): Long {
        val remainingSeconds = 60 - time.second
        return remainingSeconds * 1000L
    }

    private suspend fun calculatePages(currentDateTime: LocalDateTime, nowDateTime: LocalDateTime): PersistentList<HappeningListPagingState> {
        val (startDateTime, endDateTime) = currentDateTime.calculatePagesDateRange()

        val result = diComponent.fetchPagesUseCase.execute(startDateTime, endDateTime)

        return result.toHappeningListPagingState(nowDateTime)
    }

    private fun LocalDateTime.calculatePagesDateRange(): PagesDateRange {
        val date = this
        val daysOffset = PAGE_SIZE / 2
        val startDate = date.minus(daysOffset, DateTimeUnit.DAY)
        val endDate = date.plus(daysOffset, DateTimeUnit.DAY)
        return PagesDateRange(startDate, endDate)
    }

    private class PagesDateRange(val startDate: LocalDateTime, val endDate: LocalDateTime) {
        operator fun component1() = startDate
        operator fun component2() = endDate
    }

    companion object {
        private const val PAGE_SIZE = 15
        private const val MIN_PAGE = 0
        private const val MAX_PAGE = PAGE_SIZE - 1
    }

}