package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

sealed interface HappeningDetailSideEffect {

    data class DatePicker(val date: Long) : HappeningDetailSideEffect

    data class TimePicker(val startHours: Int, val startMinutes: Int, val endHours: Int, val endMinutes: Int) :
        HappeningDetailSideEffect

    data object Back : HappeningDetailSideEffect

}