package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.coroutines.update
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.withDate
import io.github.dmitrytsyvtsyn.fluently.core.datetime.withTime
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchSuggestionsUseCase
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchUseCase
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningInsertUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime

internal class HappeningDetailViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(
        HappeningDetailState(
            startDateTime = DateTimeExtensions.nowDateTime().plus(5, DateTimeUnit.MINUTE),
            endDateTime = DateTimeExtensions.nowDateTime().plus(65, DateTimeUnit.MINUTE)
        )
    )
    val viewState = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<HappeningDetailSideEffect>()
    val effect = _effect.asSharedFlow()

    private val diComponent = object {
        private val repository = DI.get<HappeningRepository>()

        val fetchUseCase = HappeningFetchUseCase(repository)
        val insertUseCase = HappeningInsertUseCase(repository)
        val suggestionsUseCase = HappeningFetchSuggestionsUseCase(repository)
    }

    fun handleEvent(event: HappeningDetailEvent) {
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
                _viewState.update {
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
            _viewState.update {
                copy(
                    startDateTime = initialDate.plus(5, DateTimeUnit.MINUTE),
                    endDateTime = initialDate.plus(65, DateTimeUnit.MINUTE),
                    titleError = false,
                    timeError = false
                )
            }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.TitleChanged) {
        _viewState.update {
            copy(
                title = event.title,
                titleError = false
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.DateChanged) {
        _viewState.update {
            val newDate = event.dateTime.date
            copy(
                startDateTime = startDateTime.withDate(newDate),
                endDateTime = endDateTime.withDate(newDate),
                timeError = false
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.TimeChanged) {
        val currentState = _viewState.value
        setActualizedStartEndDates(
            startDateTime = currentState.startDateTime.withTime(event.startTime),
            endDateTime = currentState.endDateTime.withTime(event.endTime)
        )
    }

    private fun handleEvent(event: HappeningDetailEvent.DateTimeChanged) {
        setActualizedStartEndDates(
            startDateTime = event.startDateTime,
            endDateTime = event.endDateTime
        )
    }

    private fun setActualizedStartEndDates(startDateTime: LocalDateTime, endDateTime: LocalDateTime) {
        val (actualStartDateTime, actualEndDateTime) = when {
            DateTimeExtensions.nowDateTime() > startDateTime -> {
                startDateTime.plus(1, DateTimeUnit.DAY) to endDateTime.plus(1, DateTimeUnit.DAY)
            }
            startDateTime > endDateTime -> {
                startDateTime to endDateTime.plus(1, DateTimeUnit.DAY)
            }
            else -> {
                startDateTime to endDateTime
            }
        }

        _viewState.update {
            copy(
                startDateTime = actualStartDateTime,
                endDateTime = actualEndDateTime,
                timeError = false
            )
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ChangeHasReminder) = viewModelScope.launch {
        val currentState = _viewState.value
        if (!currentState.hasPermissionCalendarAllowed) {
            _effect.emit(HappeningDetailSideEffect.CheckCalendarPermission)
        }
        _viewState.update { copy(hasReminder = event.hasReminder) }
    }

    private fun handleEvent(event: HappeningDetailEvent.ChangeCalendarPermissionsStatus) {
        if (event.allowed) {
            _viewState.update { copy(hasPermissionCalendarAllowed = true) }
        } else {
            _viewState.update { copy(hasReminder = !hasReminder) }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.SaveHappening) {
        val currentState = _viewState.value

        if (currentState.title.length < 3) {
            _viewState.update { copy(titleError = true) }
            return
        }

        if (currentState.startDateTime < DateTimeExtensions.nowDateTime()) {
            _viewState.update { copy(timeError = true) }
            return
        }

        viewModelScope.launch {
            val suggestionsResult = diComponent.suggestionsUseCase.execute(
                startDateTime = currentState.startDateTime,
                endDateTime = currentState.endDateTime
            )

            val suggestionRanges = suggestionsResult.toSuggestionRanges(currentState.happening)
            if (suggestionRanges.isEmpty()) {
                diComponent.insertUseCase.execute(
                    model = currentState.happening.copy(
                        title = currentState.title,
                        startDateTime = currentState.startDateTime,
                        endDateTime = currentState.endDateTime
                    ),
                    hasReminder = currentState.hasReminder
                )
                _effect.emit(HappeningDetailSideEffect.Back)
            } else {
                _viewState.update { copy(suggestionsState = HappeningSuggestionsState.Suggestions(suggestionRanges)) }
            }
        }
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowTimePicker) = viewModelScope.launch {
        val currentState = _viewState.value
        _effect.emit(
            HappeningDetailSideEffect.TimePicker(
                startTime = currentState.startDateTime.time,
                endTime = currentState.endDateTime.time,
                date = currentState.startDateTime.date
            )
        )
    }

    private fun handleEvent(event: HappeningDetailEvent.ShowDatePicker) = viewModelScope.launch {
        _effect.emit(
            HappeningDetailSideEffect.DatePicker(
                initialDate = _viewState.value.startDateTime.date,
                minDate = DateTimeExtensions.nowDateTime().date
            )
        )
    }

    private fun handleEvent(event: HappeningDetailEvent.Back) = viewModelScope.launch {
        _effect.emit(HappeningDetailSideEffect.Back)
    }

}