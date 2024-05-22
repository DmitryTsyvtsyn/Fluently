package io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.interview_list.CalendarRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.data.InterviewRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.actions.InterviewListAction
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListItemState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewModel
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
            prevDate = System.currentTimeMillis() - DAY,
            nextDate = System.currentTimeMillis() + DAY,
            totalEvents = persistentListOf(),
            filteredEvents = persistentListOf()
        )
    )
    val state: StateFlow<InterviewListState> = _state

    private val _action = MutableStateFlow<InterviewListAction>(InterviewListAction.Empty)
    val action: StateFlow<InterviewListAction> = _action

    fun init() = viewModelScope.launch {
        val state = _state.value
        val dateRange = calculateDateFilter(state.date)
        val totalEvents = repository.fetchInterviewEvents(state.date).toPersistentList()

        _state.value = state.copy(
            totalEvents = totalEvents,
            filteredEvents = totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun changeDate(newDate: Long) {
        val state = _state.value
        val dateRange = calculateDateFilter(newDate)
        _state.value = state.copy(
            date = newDate,
            prevDate = newDate - DAY,
            nextDate  = newDate + DAY,
            filteredEvents = state.totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun navigateToPreviousDay() {
        val state = _state.value
        val newDate = state.date - DAY
        val dateRange = calculateDateFilter(newDate)
        _state.value = state.copy(
            date = newDate,
            prevDate = newDate - DAY,
            nextDate = newDate + DAY,
            filteredEvents = state.totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun navigateToNextDay() {
        val state = _state.value
        val newDate = state.date + DAY
        val dateRange = calculateDateFilter(newDate)
        _state.value = state.copy(
            date = newDate,
            prevDate = state.date,
            nextDate = newDate + DAY,
            filteredEvents = state.totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun resetAction() {
        _action.value = InterviewListAction.Empty
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

    private fun PersistentList<InterviewModel>.filteredAndSorted(dateRange: LongRange): PersistentList<InterviewListItemState> {
        var current = dateRange.first
        val listStates = mutableListOf<InterviewListItemState>()
        val models = filter { it.startDate in dateRange || it.endDate in dateRange }.sortedBy { it.startDate }
        val lastIndex = models.size - 1
        val minimumTimelineInterval = CalendarRepository.minutesInMillis(15)
        models.forEachIndexed { index, model ->
            if (index == 0) {
                listStates.add(InterviewListItemState.Title("00:00"))
            }

            if ((model.startDate - current) > minimumTimelineInterval) {
                listStates.add(InterviewListItemState.Timeline(current, model.startDate))
            }

            listStates.add(InterviewListItemState.Content(model))

            if (index == lastIndex) {
                if ((dateRange.last - model.endDate) > minimumTimelineInterval) {
                    listStates.add(InterviewListItemState.Timeline(model.endDate, dateRange.last))
                }

                listStates.add(InterviewListItemState.Title("24:00"))
            }

            current = model.endDate
        }

        return listStates.toPersistentList()
    }

    private fun calculateDateFilter(date: Long): LongRange {
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        val fromDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val toDate = calendar.timeInMillis

        return fromDate until toDate
    }

    companion object {
        private const val DAY = 24 * 3600 * 1000
        private val calendar = Calendar.getInstance()
    }

}