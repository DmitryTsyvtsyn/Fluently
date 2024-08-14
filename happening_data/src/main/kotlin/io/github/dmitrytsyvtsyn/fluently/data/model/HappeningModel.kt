package io.github.dmitrytsyvtsyn.fluently.data.model

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import kotlinx.datetime.LocalDateTime

data class HappeningModel(
    val id: IdLong = IdLong.Empty,
    val eventId: IdLong = IdLong.Empty,
    val reminderId: IdLong = IdLong.Empty,
    val title: String = "",
    val startDateTime: LocalDateTime = DateTimeExtensions.nowDateTime(),
    val endDateTime: LocalDateTime = DateTimeExtensions.nowDateTime()
)