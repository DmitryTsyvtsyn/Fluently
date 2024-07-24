package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import kotlinx.datetime.LocalDateTime

data class HappeningModel(
    val id: IdLong = IdLong.Empty,
    val eventId: IdLong = IdLong.Empty,
    val reminderId: IdLong = IdLong.Empty,
    val title: String = "",
    val startDateTime: LocalDateTime = CalendarRepository.nowDateTime(),
    val endDateTime: LocalDateTime = CalendarRepository.nowDateTime()
)