package io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.actions.InterviewEventDetailAction
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.states.InterviewEventDetailState
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.states.InterviewEventBusyState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.CalendarRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.data.InterviewRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InterviewDetailViewModel : ViewModel() {

    private val repository = InterviewRepository(DI.sqliteHelper, DI.platformAPI)

    private val _state = MutableStateFlow(
        InterviewEventDetailState(
            title = "",
            startDate = System.currentTimeMillis() + CalendarRepository.minutesInMillis(5L),
            endDate = System.currentTimeMillis() + CalendarRepository.minutesInMillis(65L)
        )
    )
    val state: StateFlow<InterviewEventDetailState> = _state

    private val _action = MutableStateFlow<InterviewEventDetailAction>(InterviewEventDetailAction.Empty)
    val action: StateFlow<InterviewEventDetailAction> = _action

    fun init(id: Long, initialDate: Long) {
        if (id >= 0) {
            viewModelScope.launch {
                val state = _state.value
                val event = repository.fetchInterviewEvent(id)
                _state.value = state.copy(
                    id = event.id,
                    eventId = event.eventId,
                    reminderId = event.reminderId,
                    title = event.title,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    titleError = false,
                    timeError = false,
                    hasReminder = event.reminderId >= 0
                )
            }
        } else {
            val actualDate = CalendarRepository.matchDateWithTime(initialDate, CalendarRepository.nowDate())
            _state.value = _state.value.copy(
                startDate = CalendarRepository.plusMinutes(actualDate, 5L),
                endDate = CalendarRepository.plusMinutes(actualDate, 65L)
            )
        }
    }

    fun titleChanged(title: String) {
        _state.value = _state.value.copy(
            title = title,
            titleError = false
        )
    }

    fun dateChanged(date: Long) {
        val state = _state.value

        _state.value = state.copy(
            startDate = CalendarRepository.matchTimeWithDate(state.startDate, date),
            endDate = CalendarRepository.matchTimeWithDate(state.endDate, date)
        )
    }

    fun timeChanged(startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int) {
        val state = _state.value

        val currentTime = CalendarRepository.nowDate()

        val startDate = CalendarRepository.matchDateWithHoursAndMinutes(state.startDate, startHours, startMinutes)
        val endDate = CalendarRepository.matchDateWithHoursAndMinutes(state.endDate, endHours, endMinutes)
        val (actualStartDate, actualEndDate) = if (currentTime > startDate) {
            CalendarRepository.plusDays(startDate, 1) to CalendarRepository.plusDays(endDate, 1)
        } else if (currentTime > endDate) {
            startDate to CalendarRepository.plusDays(endDate, 1)
        } else {
            startDate to endDate
        }

        _state.value = state.copy(startDate = actualStartDate, endDate = actualEndDate)
    }

    fun changeHasReminder(value: Boolean) {
        _state.value = _state.value.copy(hasReminder = value)
    }

    fun save() {
        val state = _state.value
        if (state.title.length < 3) {
            _state.value = state.copy(titleError = true)

            return
        }

        viewModelScope.launch {
            val dateRange = state.startDate..state.endDate

            val alreadyScheduledEvents = mutableListOf<InterviewModel>()
            val suggestionRanges = mutableListOf<LongRange>()
            var currentDate = state.startDate

            val minimumInterval = CalendarRepository.plusMinutes((dateRange.last - dateRange.first), 10)

            repository.fetchInterviewEvents()
                .filter { it.endDate > state.startDate && it.id != state.id }
                .sortedBy { it.startDate }
                .forEach { interview ->
                    if (interview.startDate in dateRange || interview.endDate in dateRange) {
                        alreadyScheduledEvents.add(interview)
                    }
                    if (currentDate < interview.startDate && suggestionRanges.size < 2) {
                        val differenceBetweenEndAndStartDates = interview.startDate - currentDate
                        if (differenceBetweenEndAndStartDates > minimumInterval) {
                            val startSuggestionDate = CalendarRepository.plusMinutes(currentDate, 5)
                            suggestionRanges.add(startSuggestionDate..startSuggestionDate + minimumInterval)
                        }
                    }
                    currentDate = interview.endDate
                }

            while (suggestionRanges.size < 2) {
                suggestionRanges.add(currentDate..currentDate + minimumInterval)
                currentDate += 2 * minimumInterval
            }

            if (alreadyScheduledEvents.isNotEmpty()) {
                _state.value = state.copy(
                    busyState = InterviewEventBusyState.BusyWithSuggestions(
                        startDate = _state.value.startDate,
                        endDate = _state.value.endDate,
                        scheduledStates = alreadyScheduledEvents.toPersistentList(),
                        suggestionRanges = suggestionRanges.toPersistentList()
                    )
                )
            } else {
                repository.addInterviewEvent(
                    model = InterviewModel(
                        id = state.id,
                        eventId = state.eventId,
                        reminderId = state.reminderId,
                        title = state.title,
                        startDate = state.startDate,
                        endDate = state.endDate,
                    ),
                    hasReminder = state.hasReminder
                )
                _action.value = InterviewEventDetailAction.Back
            }
        }
    }

    fun navigateToTimePicker() {
        val state = _state.value
        calendar.timeInMillis = state.startDate
        val startHours = calendar.get(Calendar.HOUR_OF_DAY)
        val startMinutes = calendar.get(Calendar.MINUTE)

        calendar.timeInMillis = state.endDate
        val endHours = calendar.get(Calendar.HOUR_OF_DAY)
        val endMinutes = calendar.get(Calendar.MINUTE)

        _action.value = InterviewEventDetailAction.TimePicker(
            startHours = startHours,
            startMinutes = startMinutes,
            endHours = endHours,
            endMinutes = endMinutes,
        )
    }

    fun navigateToDatePicker() {
        _action.value = InterviewEventDetailAction.DatePicker(_state.value.startDate)
    }

    fun navigateToBack() {
        _action.value = InterviewEventDetailAction.Back
    }

    fun resetAction() {
        _action.value = InterviewEventDetailAction.Empty
    }

    companion object {
        private val calendar = Calendar.getInstance()
    }

}