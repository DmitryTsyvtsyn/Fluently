package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewSideEffect

sealed interface HappeningDetailSideEffect : ViewSideEffect {

    data class DatePicker(val date: Long) : HappeningDetailSideEffect

    data class TimePicker(val startHours: Int, val startMinutes: Int, val endHours: Int, val endMinutes: Int) :
        HappeningDetailSideEffect

    data object Back : HappeningDetailSideEffect

}