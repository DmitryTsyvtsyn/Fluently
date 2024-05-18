package io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.actions.InterviewEventDetailAction
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.states.InterviewEventDetailState
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.states.InterviewEventScheduledState
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data.InterviewEventRepository
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventStatus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InterviewEventDetailViewModel : ViewModel() {

    private val repository = InterviewEventRepository(DI.sqliteHelper, DI.platformAPI)

    private val _state = MutableStateFlow(
        InterviewEventDetailState(
            title = "",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 60 * MINUTE
        )
    )
    val state: StateFlow<InterviewEventDetailState> = _state

    private val _action = MutableStateFlow<InterviewEventDetailAction>(InterviewEventDetailAction.Empty)
    val action: StateFlow<InterviewEventDetailAction> = _action

    fun init(id: Long) {
        if (id < 0) return

        viewModelScope.launch {
            val state = _state.value
            val event = repository.fetchInterviewEvent(id, state.startDate)
            _state.value = state.copy(
                id = event.id,
                eventId = event.id,
                reminderId = event.reminderId,
                title = event.title,
                startDate = event.startDate,
                endDate = event.endDate,
                titleError = false,
                timeError = false,
                rescheduleInterview = event.endDate > System.currentTimeMillis(),
                hasReminder = event.id >= 0
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

        calendar.timeInMillis = date
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = state.startDate
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val newStartDate = calendar.timeInMillis

        calendar.timeInMillis = state.endDate
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear)
        val newEndDate = calendar.timeInMillis

        _state.value = state.copy(
            startDate = newStartDate,
            endDate = newEndDate
        )
    }

    fun timeChanged(startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int) {
        val state = _state.value

        calendar.timeInMillis = state.startDate
        calendar.set(Calendar.HOUR_OF_DAY, startHours)
        calendar.set(Calendar.MINUTE, startMinutes)
        val startDate = calendar.timeInMillis

        calendar.timeInMillis = state.endDate
        calendar.set(Calendar.HOUR_OF_DAY, endHours)
        calendar.set(Calendar.MINUTE, endMinutes)
        val endDate = calendar.timeInMillis

        _state.value = state.copy(startDate = startDate, endDate = endDate)
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

            val alreadyScheduledEvents = mutableListOf<InterviewEventModel>()
            val freeRanges = mutableListOf<LongRange>()
            var currentDate = state.startDate

            repository.fetchInterviewEvents(state.startDate)
                .filter { it.endDate > state.startDate && it.id != state.id }
                .sortedBy { it.startDate }
                .forEach {
                    if (it.startDate in dateRange || it.endDate in dateRange) {
                        alreadyScheduledEvents.add(it)
                    }
                    if (currentDate < it.startDate && freeRanges.size < 2) {
                        var dateRange = it.startDate - currentDate
                        while (dateRange > 60 * MINUTE) {
                            freeRanges.add(currentDate..60 * MINUTE)
                            dateRange -= 60 * MINUTE
                        }
                    }
                    currentDate = it.endDate
                }

            while (freeRanges.size < 2) {
                freeRanges.add(currentDate..currentDate + 60 * MINUTE)
                currentDate += 120 * MINUTE
            }

            if (alreadyScheduledEvents.isNotEmpty()) {
                _state.value = state.copy(
                    alreadyScheduledState = InterviewEventScheduledState.Content(
                        scheduledStates = alreadyScheduledEvents.toPersistentList(),
                        freeRanges = freeRanges.toPersistentList()
                    )
                )
            } else {
                repository.addInterviewEvent(
                    model = InterviewEventModel(
                        id = state.id,
                        eventId = state.eventId,
                        reminderId = state.reminderId,
                        title = state.title,
                        startDate = state.startDate,
                        endDate = state.endDate,
                        status = InterviewEventStatus.ACTUAL
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

    fun resetAction() {
        _action.value = InterviewEventDetailAction.Empty
    }

    companion object {
        private val calendar = Calendar.getInstance()
        private const val MINUTE = 60 * 1000
    }

}