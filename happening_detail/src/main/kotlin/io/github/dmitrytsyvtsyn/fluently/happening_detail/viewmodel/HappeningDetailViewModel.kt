package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

internal class HappeningDetailViewModel : BaseViewModel<HappeningDetailEvent, HappeningDetailState, HappeningDetailSideEffect>(
    HappeningDetailState(
        startDate = CalendarRepository.nowDate() + CalendarRepository.minutesInMillis(5L),
        endDate = CalendarRepository.nowDate() + CalendarRepository.minutesInMillis(65L)
    )
) {

    private val repository = DI.get<HappeningRepository>()

    override fun handleEvent(event: HappeningDetailEvent) {
        when (event) {
            is HappeningDetailEvent.TitleChanged -> handleEvent(event)
            is HappeningDetailEvent.DateChanged -> handleEvent(event)
            is HappeningDetailEvent.TimeChanged -> handleEvent(event)
            is HappeningDetailEvent.ChangeHasReminder -> handleEvent(event)
            is HappeningDetailEvent.SaveHappening -> handleEvent(event)
            is HappeningDetailEvent.ShowTimePicker -> handleEvent(event)
            is HappeningDetailEvent.ShowDatePicker -> handleEvent(event)
            is HappeningDetailEvent.Back -> handleEvent(event)
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.TitleChanged) {
        setState {
            copy(
                title = event.title,
                titleError = false
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.DateChanged) {
        setState {
            copy(
                startDate = CalendarRepository.matchTimeWithDate(startDate, event.date),
                endDate = CalendarRepository.matchTimeWithDate(endDate, event.date)
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.TimeChanged) {
        val startDateTime = CalendarRepository.timeFromHoursAndMinutes(event.startHours, event.startMinutes)
        val endDateTime = CalendarRepository.timeFromHoursAndMinutes(event.endHours, event.endMinutes)

        val startDate = CalendarRepository.matchDateWithHoursAndMinutes(
            viewState.value.startDate,
            event.startHours,
            event.startMinutes
        )
        val endDate = CalendarRepository.matchDateWithHoursAndMinutes(
            viewState.value.endDate,
            event.endHours,
            event.endMinutes
        )
        val (actualStartDate, actualEndDate) = if (CalendarRepository.nowDate() > startDate) {
            CalendarRepository.plusDays(startDate, 1) to CalendarRepository.plusDays(endDate, 1)
        } else if (startDateTime > endDateTime) {
            startDate to CalendarRepository.plusDays(endDate, 1)
        } else {
            startDate to endDate
        }

        setState {
            copy(startDate = actualStartDate, endDate = actualEndDate)
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ChangeHasReminder) {
        setState {
            copy(hasReminder = event.hasReminder)
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.SaveHappening) {
        val state = viewState.value
        if (state.title.length < 3) {
            setState {
                copy(titleError = true)
            }

            return
        }

        viewModelScope.launch {
            val dateRange = state.startDate..state.endDate

            val alreadyScheduledEvents = mutableListOf<io.github.dmitrytsyvtsyn.fluently.data.HappeningModel>()
            val suggestionRanges = mutableListOf<LongRange>()
            var currentDate = state.startDate

            val minimumInterval = CalendarRepository.plusMinutes((dateRange.last - dateRange.first), 10)

            repository.fetch(state.startDate, state.endDate)
                .filter { it.id != state.id }
                .forEach { item ->
                    if (item.startDate in dateRange || item.endDate in dateRange) {
                        alreadyScheduledEvents.add(item)
                    }
                    if (currentDate < item.startDate && suggestionRanges.size < 2) {
                        val differenceBetweenEndAndStartDates = item.startDate - currentDate
                        if (differenceBetweenEndAndStartDates > minimumInterval) {
                            val startSuggestionDate = CalendarRepository.plusMinutes(currentDate, 5)
                            suggestionRanges.add(startSuggestionDate..startSuggestionDate + minimumInterval)
                        }
                    }
                    currentDate = item.endDate
                }

            while (suggestionRanges.size < 2) {
                suggestionRanges.add(currentDate..currentDate + minimumInterval)
                currentDate += 2 * minimumInterval
            }

            if (alreadyScheduledEvents.isNotEmpty()) {
                setState {
                    copy(
                        busyState = InterviewEventBusyState.BusyWithSuggestions(
                            startDate = startDate,
                            endDate = endDate,
                            scheduledStates = alreadyScheduledEvents.toPersistentList(),
                            suggestionRanges = suggestionRanges.toPersistentList()
                        )
                    )
                }
            } else {
                repository.insert(
                    model = HappeningModel(
                        id = state.id,
                        eventId = state.eventId,
                        reminderId = state.reminderId,
                        title = state.title,
                        startDate = state.startDate,
                        endDate = state.endDate,
                    ),
                    hasReminder = state.hasReminder
                )
                setEffect(HappeningDetailSideEffect.Back)
            }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowTimePicker) {
        val state = viewState.value

        val (startHours, startMinutes) = CalendarRepository.hoursMinutes(state.startDate)
        val (endHours, endMinutes) = CalendarRepository.hoursMinutes(state.endDate)

        setEffect(
            HappeningDetailSideEffect.TimePicker(
                startHours = startHours,
                startMinutes = startMinutes,
                endHours = endHours,
                endMinutes = endMinutes,
            )
        )
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowDatePicker) {
        setEffect(HappeningDetailSideEffect.DatePicker(viewState.value.startDate))
    }

    private fun handleEvent(event: HappeningDetailEvent.Back) {
        setEffect(HappeningDetailSideEffect.Back)
    }

    fun init(id: Long, initialDate: Long) {
        if (id >= 0) {
            viewModelScope.launch {
                val event = repository.fetch(id)
                setState {
                    copy(
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
            }
        } else {
            val actualDate = CalendarRepository.matchDateWithTime(initialDate, CalendarRepository.nowDate())
            setState {
                copy(
                    startDate = CalendarRepository.plusMinutes(actualDate, 5L),
                    endDate = CalendarRepository.plusMinutes(actualDate, 65L)
                )
            }
        }
    }

}