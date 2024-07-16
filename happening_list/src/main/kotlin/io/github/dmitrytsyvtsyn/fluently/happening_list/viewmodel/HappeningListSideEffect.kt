package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong

internal sealed interface HappeningListSideEffect {

    class ShowCalendar(val id: IdLong) : HappeningListSideEffect

    class ShowDetail(val id: IdLong = IdLong.Empty, val date: Long = CalendarRepository.nowDate()) : HappeningListSideEffect

    class ShowDatePicker(val date: Long) : HappeningListSideEffect

}