package io.github.dmitrytsyvtsyn.fluently.data.model

import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import kotlinx.datetime.LocalDateTime

data class HappeningModel(
    val id: IdLong = IdLong.Empty,
    val eventId: IdLong = IdLong.Empty,
    val reminderId: IdLong = IdLong.Empty,
    val title: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
) {
    companion object {
        val Empty = HappeningModel(
            startDateTime = LocalDateTime(1000, 9, 4, 0, 0),
            endDateTime = LocalDateTime(1000, 9, 4, 0, 0)
        )
    }
}