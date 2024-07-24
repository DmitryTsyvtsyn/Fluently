package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import kotlinx.datetime.LocalDateTime

internal sealed interface HappeningListSideEffect {

    class ShowCalendar(val id: IdLong) : HappeningListSideEffect

    class ShowDetail(val id: IdLong = IdLong.Empty, val dateTime: LocalDateTime) : HappeningListSideEffect

    class ShowDatePicker(val dateTime: LocalDateTime) : HappeningListSideEffect

}