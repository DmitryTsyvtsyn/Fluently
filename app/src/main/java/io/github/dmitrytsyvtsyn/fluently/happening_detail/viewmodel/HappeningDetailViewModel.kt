package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import android.provider.CalendarContract.CalendarEntity
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.InterviewRepository
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class HappeningDetailViewModel : BaseViewModel<HappeningDetailEvent, HappeningDetailState, HappeningDetailSideEffect>() {

    private val repository = InterviewRepository(DI.sqliteHelper, DI.platformAPI)

    override fun initialState() =
        HappeningDetailState(
            title = "",
            startDate = System.currentTimeMillis() + CalendarRepository.minutesInMillis(5L),
            endDate = System.currentTimeMillis() + CalendarRepository.minutesInMillis(65L)
        )

    override fun handleEvents(event: HappeningDetailEvent) {
        when (event) {
            is HappeningDetailEvent.TitleChanged -> {
                setState {
                    copy(
                        title = event.title,
                        titleError = false
                    )
                }
            }
            is HappeningDetailEvent.DateChanged -> {
                setState {
                    copy(
                        startDate = CalendarRepository.matchTimeWithDate(startDate, event.date),
                        endDate = CalendarRepository.matchTimeWithDate(endDate, event.date)
                    )
                }
            }
            is HappeningDetailEvent.TimeChanged -> {
                val currentTime = CalendarRepository.currentTime()
                val startDateTime = CalendarRepository.timeFromHoursAndMinutes(event.startHours, event.startMinutes)
                val endDateTime = CalendarRepository.timeFromHoursAndMinutes(event.endHours, event.endMinutes)

                val startDate = CalendarRepository.matchDateWithHoursAndMinutes(viewState.value.startDate, event.startHours, event.startMinutes)
                val endDate = CalendarRepository.matchDateWithHoursAndMinutes(viewState.value.endDate, event.endHours, event.endMinutes)
                val (actualStartDate, actualEndDate) = if (currentTime > startDateTime) {
                    CalendarRepository.plusDays(startDate, 1) to CalendarRepository.plusDays(endDate, 1)
                } else if (currentTime > endDateTime) {
                    startDate to CalendarRepository.plusDays(endDate, 1)
                } else {
                    startDate to endDate
                }

                setState {
                    copy(startDate = actualStartDate, endDate = actualEndDate)
                }
            }
            is HappeningDetailEvent.ChangeHasReminder -> {
                setState {
                    copy(hasReminder = event.hasReminder)
                }
            }
            is HappeningDetailEvent.SaveHappening -> {
                val state = viewState.value
                if (state.title.length < 3) {
                    setState {
                        copy(titleError = true)
                    }

                    return
                }

                viewModelScope.launch {
                    val dateRange = state.startDate..state.endDate

                    val alreadyScheduledEvents = mutableListOf<HappeningModel>()
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
                        repository.addInterviewEvent(
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
            is HappeningDetailEvent.ShowTimePicker -> {
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
            is HappeningDetailEvent.ShowDatePicker -> {
                setEffect(HappeningDetailSideEffect.DatePicker(viewState.value.startDate))
            }
            is HappeningDetailEvent.Back -> {
                setEffect(HappeningDetailSideEffect.Back)
            }
        }
    }

    fun init(id: Long, initialDate: Long) {
        if (id >= 0) {
            viewModelScope.launch {
                val event = repository.fetchInterviewEvent(id)
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