package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import kotlinx.datetime.LocalDateTime

internal data class HappeningDetailState(
    val happening: HappeningModel = HappeningModel(),
    val title: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val titleError: Boolean = false,
    val timeError: Boolean = false,
    val suggestionsState: HappeningSuggestionsState = HappeningSuggestionsState.NoSuggestions,
    val hasReminder: Boolean = false,
    val hasPermissionCalendarAllowed: Boolean = false,
)

