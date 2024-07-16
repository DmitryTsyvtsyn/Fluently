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
            is HappeningDetailEvent.Init -> handleEvent(event)
            is HappeningDetailEvent.TitleChanged -> handleEvent(event)
            is HappeningDetailEvent.DateChanged -> handleEvent(event)
            is HappeningDetailEvent.TimeChanged -> handleEvent(event)
            is HappeningDetailEvent.ChangeHasReminder -> handleEvent(event)
            is HappeningDetailEvent.SaveHappening -> handleEvent(event)
            is HappeningDetailEvent.ShowTimePicker -> handleEvent(event)
            is HappeningDetailEvent.ShowDatePicker -> handleEvent(event)
            is HappeningDetailEvent.Back -> handleEvent(event)
            is HappeningDetailEvent.ChangeCalendarPermissionsStatus -> handleEvent(event)
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.Init) {
        val happeningId = event.id
        if (happeningId.isNotEmpty) {
            viewModelScope.launch {
                val happening = repository.fetch(happeningId)
                setState {
                    copy(
                        happening = happening,
                        title = happening.title,
                        startDate = happening.startDate,
                        endDate = happening.endDate,
                        titleError = false,
                        timeError = false,
                        hasReminder = happening.reminderId.isNotEmpty
                    )
                }
            }
        } else {
            val actualDate = CalendarRepository.matchDateWithTime(event.initialDate, CalendarRepository.nowDate())
            setState {
                copy(
                    startDate = CalendarRepository.plusMinutes(actualDate, 5L),
                    endDate = CalendarRepository.plusMinutes(actualDate, 65L)
                )
            }
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
        val state = viewState.value
        if (!state.hasPermissionCalendarAllowed) {
            setEffect(HappeningDetailSideEffect.CheckCalendarPermission)
        }
        setState {
            copy(hasReminder = event.hasReminder)
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ChangeCalendarPermissionsStatus) {
        if (event.allowed) {
            setState {
                copy(hasPermissionCalendarAllowed = true)
            }
        } else {
            setState {
                copy(hasReminder = false)
            }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.SaveHappening) {
        val minimumTitleSize = 3

        val state = viewState.value
        if (state.title.length < minimumTitleSize) {
            setState {
                copy(titleError = true)
            }
            return
        }

        viewModelScope.launch {
            val result = checkAlreadyScheduledEvents(state.startDate, state.endDate)
            if (result is CheckAlreadyScheduledEventsResult.SuggestionRanges) {
                setState {
                    copy(
                        busyState = InterviewEventBusyState.BusyWithSuggestions(
                            startDate = startDate,
                            endDate = endDate,
                            suggestionRanges = result.ranges.toPersistentList()
                        )
                    )
                }
            } else {
                repository.insert(
                    model = state.happening.copy(
                        title = state.title,
                        startDate = state.startDate,
                        endDate = state.endDate
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

    private suspend fun checkAlreadyScheduledEvents(startDate: Long, endDate: Long): CheckAlreadyScheduledEventsResult {
        val alreadyScheduledEvents = mutableListOf<HappeningModel>()
        val suggestionRanges = mutableListOf<LongRange>()
        var currentDate = startDate

        val minimumSuggestionRangesCount = 2
        val minimumInterval = CalendarRepository.plusMinutes((endDate - startDate), 10)

        val currentHappeningId = viewState.value.happening.id
        val dateRange = startDate..endDate

        val happeningsByStartDateEndDateRange = repository.fetch(startDate, endDate)
        val happeningsByStartDateEndDateRangeSize = happeningsByStartDateEndDateRange.size
        var index = 0
        while (index < happeningsByStartDateEndDateRangeSize) {
            val happening = happeningsByStartDateEndDateRange[index]

            val isCurrentHappening = happening.id == currentHappeningId
            if (isCurrentHappening) {
                index++
                continue
            }

            if (happening.startDate in dateRange || happening.endDate in dateRange) {
                alreadyScheduledEvents.add(happening)
            }

            if (currentDate < happening.startDate && suggestionRanges.size < minimumSuggestionRangesCount) {
                val differenceBetweenEndAndStartDates = happening.startDate - currentDate
                if (differenceBetweenEndAndStartDates > minimumInterval) {
                    val startSuggestionDate = CalendarRepository.plusMinutes(currentDate, 5)
                    suggestionRanges.add(startSuggestionDate..startSuggestionDate + minimumInterval)
                }
            }
            currentDate = happening.endDate

            index++
        }

        if (alreadyScheduledEvents.isEmpty()) {
            return CheckAlreadyScheduledEventsResult.NoScheduledEvents
        }

        while (suggestionRanges.size < 2) {
            suggestionRanges.add(currentDate..currentDate + minimumInterval)
            currentDate += 2 * minimumInterval
        }
        return CheckAlreadyScheduledEventsResult.SuggestionRanges(suggestionRanges)
    }

    private sealed interface CheckAlreadyScheduledEventsResult {
        data object NoScheduledEvents : CheckAlreadyScheduledEventsResult
        class SuggestionRanges(val ranges: List<LongRange>) : CheckAlreadyScheduledEventsResult
    }

}