package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import kotlinx.datetime.LocalDateTime

internal data class HappeningDetailState(
    val happening: HappeningModel = HappeningModel.Empty,
    val title: String = "",
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val titleError: Boolean = false,
    val timeActualError: Boolean = false,
    val timePeriodError: Boolean = false,
    val suggestionsState: HappeningSuggestionsState = HappeningSuggestionsState.NoSuggestions,
    val hasReminder: Boolean = false,
    val hasPermissionCalendarAllowed: Boolean = false,
) {
    
    val isStartEndDateTimesValid: Boolean
        get() {
            val isDateTimeChanged = if (happening != HappeningModel.Empty) {
                happening.startDateTime != startDateTime || happening.endDateTime != endDateTime
            } else {
                true
            }
            
            return if (isDateTimeChanged) {
                startDateTime > DateTimeExtensions.nowDateTime()
            } else {
                true
            }
        }
    
}

