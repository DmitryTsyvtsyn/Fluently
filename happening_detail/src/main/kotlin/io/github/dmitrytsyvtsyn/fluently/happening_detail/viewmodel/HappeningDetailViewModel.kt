package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime

internal class HappeningDetailViewModel : BaseViewModel<HappeningDetailEvent, HappeningDetailState, HappeningDetailSideEffect>(
    HappeningDetailState(
        startDateTime = CalendarRepository.nowDateTime().plus(5, DateTimeUnit.MINUTE),
        endDateTime = CalendarRepository.nowDateTime().plus(65, DateTimeUnit.MINUTE)
    )
) {

    private val diComponent = object {
        private val repository = DI.get<HappeningRepository>()

        val fetchUseCase = HappeningFetchUseCase(repository)
        val insertUseCase = HappeningInsertUseCase(repository)
        val suggestionsUseCase = HappeningFetchSuggestionsUseCase(repository)
    }

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
                val happening = diComponent.fetchUseCase.execute(happeningId)
                setState {
                    copy(
                        happening = happening,
                        title = happening.title,
                        startDateTime = happening.startDateTime,
                        endDateTime = happening.endDateTime,
                        titleError = false,
                        timeError = false,
                        hasReminder = happening.reminderId.isNotEmpty
                    )
                }
            }
        } else {
            val initialDate = event.initialDateTime
            setState {
                copy(
                    startDateTime = initialDate.plus(5, DateTimeUnit.MINUTE),
                    endDateTime = initialDate.plus(65, DateTimeUnit.MINUTE)
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
            val newDate = event.dateTime.date
            copy(
                startDateTime = LocalDateTime(newDate, startDateTime.time),
                endDateTime = LocalDateTime(newDate, endDateTime.time)
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.TimeChanged) {
        val state = viewState.value

        val newStartDateTime = LocalDateTime(state.startDateTime.date, event.startTime)
        val newEndDateTime = LocalDateTime(state.endDateTime.date, event.endTime)

        val (actualStartDateTime, actualEndDateTime) = when {
            CalendarRepository.nowDateTime() > newStartDateTime -> {
                newStartDateTime.plus(1, DateTimeUnit.DAY) to newEndDateTime.plus(1, DateTimeUnit.DAY)
            }
            newStartDateTime > newEndDateTime -> {
                newStartDateTime to newEndDateTime.plus(1, DateTimeUnit.DAY)
            }
            else -> {
                newStartDateTime to newEndDateTime
            }
        }

        setState {
            copy(startDateTime = actualStartDateTime, endDateTime = actualEndDateTime)
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
            val result = diComponent.suggestionsUseCase.execute(
                happening = viewState.value.happening,
                startDateTime = state.startDateTime,
                endDateTime = state.endDateTime
            )
            if (result is HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.SuggestionRanges) {
                setState {
                    copy(
                        busyState = InterviewEventBusyState.BusyWithSuggestions(
                            startDateTime = startDateTime,
                            endDateTime = endDateTime,
                            suggestionRanges = result.ranges.toPersistentList()
                        )
                    )
                }
            } else {
                diComponent.insertUseCase.execute(
                    model = state.happening.copy(
                        title = state.title,
                        startDateTime = state.startDateTime,
                        endDateTime = state.endDateTime
                    ),
                    hasReminder = state.hasReminder
                )
                setEffect(HappeningDetailSideEffect.Back)
            }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowTimePicker) {
        val state = viewState.value

        setEffect(
            HappeningDetailSideEffect.TimePicker(
                startTime = state.startDateTime.time,
                endTime = state.endDateTime.time
            )
        )
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowDatePicker) {
        setEffect(HappeningDetailSideEffect.DatePicker(viewState.value.startDateTime))
    }

    private fun handleEvent(event: HappeningDetailEvent.Back) {
        setEffect(HappeningDetailSideEffect.Back)
    }

}