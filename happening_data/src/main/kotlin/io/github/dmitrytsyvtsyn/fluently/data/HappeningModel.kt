package io.github.dmitrytsyvtsyn.fluently.data

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong

data class HappeningModel(
    val id: IdLong = IdLong.Empty,
    val eventId: IdLong = IdLong.Empty,
    val reminderId: IdLong = IdLong.Empty,
    val title: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L
)