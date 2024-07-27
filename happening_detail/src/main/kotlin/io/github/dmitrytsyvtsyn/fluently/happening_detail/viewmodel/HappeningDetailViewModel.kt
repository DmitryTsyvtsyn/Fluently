package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchSuggestionsUseCase
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchUseCase
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningInsertUseCase
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
            is HappeningDetailEvent.DateTimeChanged -> handleEvent(event)
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

        val startDateTime = LocalDateTime(state.startDateTime.date, event.startTime)
        val endDateTime = LocalDateTime(state.endDateTime.date, event.endTime)

        setActualizedStartEndDates(startDateTime, endDateTime)
    }

    private fun handleEvent(event: HappeningDetailEvent.DateTimeChanged) {
        setActualizedStartEndDates(event.startDateTime, event.endDateTime)
    }

    private fun setActualizedStartEndDates(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        val (actualStartDateTime, actualEndDateTime) = when {
            CalendarRepository.nowDateTime() > startDateTime -> {
                startDateTime.plus(1, DateTimeUnit.DAY) to endDateTime.plus(1, DateTimeUnit.DAY)
            }
            startDateTime > endDateTime -> {
                startDateTime to endDateTime.plus(1, DateTimeUnit.DAY)
            }
            else -> {
                startDateTime to endDateTime
            }
        }

        setState {
            copy(
                startDateTime = actualStartDateTime,
                endDateTime = actualEndDateTime
            )
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
            val suggestionsResult = diComponent.suggestionsUseCase.execute(
                startDateTime = state.startDateTime,
                endDateTime = state.endDateTime
            )

            var isFreeRange = true
            if (suggestionsResult is HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.Suggestions) {
                val plannedHappenings = suggestionsResult.plannedHappenings
                if (plannedHappenings.size > 1 || plannedHappenings.first() != state.happening) {
                    setState {
                        copy(
                            suggestionsState = HappeningSuggestionsState.Suggestions(
                                ranges = suggestionsResult.ranges.toPersistentList()
                            )
                        )
                    }
                    isFreeRange = false
                }
            }

            if (isFreeRange) {
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