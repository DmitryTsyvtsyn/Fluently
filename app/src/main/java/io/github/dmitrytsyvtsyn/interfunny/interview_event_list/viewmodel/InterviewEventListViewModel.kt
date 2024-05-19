package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.CalendarRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data.InterviewEventRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.actions.InterviewEventListAction
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventListItemState
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventListState
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InterviewEventListViewModel : ViewModel() {

    private val repository = InterviewEventRepository(DI.sqliteHelper, DI.platformAPI)

    private val _state: MutableStateFlow<InterviewEventListState> = MutableStateFlow(
        InterviewEventListState(
            date = System.currentTimeMillis(),
            prevDate = System.currentTimeMillis() - DAY,
            nextDate = System.currentTimeMillis() + DAY,
            totalEvents = persistentListOf(),
            filteredEvents = persistentListOf()
        )
    )
    val state: StateFlow<InterviewEventListState> = _state

    private val _action = MutableStateFlow<InterviewEventListAction>(InterviewEventListAction.Empty)
    val action: StateFlow<InterviewEventListAction> = _action

    fun init() = viewModelScope.launch {
        updateState {
            val dateRange = calculateDateFilter(date)
            val totalEvents = repository.fetchInterviewEvents(date).toPersistentList()

            copy(
                totalEvents = totalEvents,
                filteredEvents = totalEvents.filteredAndSorted(dateRange)
            )
        }

    }

    fun changeDate(newDate: Long) = updateState {
        val dateRange = calculateDateFilter(newDate)
        copy(
            date = newDate,
            prevDate = newDate - DAY,
            nextDate  = newDate + DAY,
            filteredEvents = totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun backDay() = updateState {
        val newDate = date - DAY
        val dateRange = calculateDateFilter(newDate)
        copy(
            date = newDate,
            prevDate = newDate - DAY,
            nextDate = newDate + DAY,
            filteredEvents = totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun forwardDay() = updateState {
        val newDate = date + DAY
        val dateRange = calculateDateFilter(newDate)
        copy(
            date = newDate,
            prevDate = date,
            nextDate = newDate + DAY,
            filteredEvents = totalEvents.filteredAndSorted(dateRange)
        )
    }

    fun resetAction() {
        _action.value = InterviewEventListAction.Empty
    }

    fun showDatePicker() {
        _action.value = InterviewEventListAction.ShowDatePicker(_state.value.date)
    }

    fun navigateToDetail(id: Long) {
        _action.value = InterviewEventListAction.Detail(id)
    }

    fun removeInterviewEvent(id: Long, eventId: Long, reminderId: Long) = viewModelScope.launch {
        repository.removeInterviewEvent(id, eventId, reminderId)
        init()
    }

    fun addInterviewEvent() {
        _action.value = InterviewEventListAction.Detail(-1)
    }

    private fun PersistentList<InterviewEventModel>.filteredAndSorted(dateRange: LongRange): PersistentList<InterviewEventListItemState> {
        var current = dateRange.first
        val listStates = mutableListOf<InterviewEventListItemState>()
        val models = filter { it.startDate in dateRange || it.endDate in dateRange }.sortedBy { it.startDate }
        val lastIndex = models.size - 1
        val minimumTimelineInterval = CalendarRepository.minutesInMillis(15)
        models.forEachIndexed { index, model ->
            if (index == 0) {
                listStates.add(InterviewEventListItemState.Title("00:00"))
            }

            if ((model.startDate - current) > minimumTimelineInterval) {
                listStates.add(InterviewEventListItemState.Timeline(current, model.startDate))
            }

            listStates.add(InterviewEventListItemState.Content(model))

            if (index == lastIndex) {
                if ((dateRange.last - model.endDate) > minimumTimelineInterval) {
                    listStates.add(InterviewEventListItemState.Timeline(model.endDate, dateRange.last))
                }

                listStates.add(InterviewEventListItemState.Title("24:00"))
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

    private inline fun updateState(updater: InterviewEventListState.() -> InterviewEventListState) {
        _state.value = updater.invoke(_state.value)
    }

    companion object {
        private const val DAY = 24 * 3600 * 1000
        private val calendar = Calendar.getInstance()
    }

}