package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

internal sealed interface HappeningDetailSideEffect {

    class DatePicker(val date: Long) : HappeningDetailSideEffect

    class TimePicker(
        val startHours: Int,
        val startMinutes: Int,
        val endHours: Int,
        val endMinutes: Int
    ) : HappeningDetailSideEffect

    data object Back : HappeningDetailSideEffect

}