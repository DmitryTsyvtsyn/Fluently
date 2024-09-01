package io.github.dmitrytsyvtsyn.fluently.happening_detail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.happening_detail.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDateTime

@Composable
fun Suggestions(
    modifier: Modifier = Modifier,
    suggestionRanges: PersistentList<ClosedRange<LocalDateTime>>,
    onSuggestionClick: (startDateTime: LocalDateTime, endDateTime: LocalDateTime) -> Unit,
    maxSuggestionsShowed: Int = 3
) {
    Column(modifier = modifier) {
        FluentlyText(
            text = stringResource(id = R.string.you_have_already_scheduled_events),
            color = FluentlyTheme.colors.errorColor,
            style = FluentlyTheme.typography.caption3
        )

        Spacer(Modifier.size(12.dp))

        suggestionRanges.take(maxSuggestionsShowed).forEach { suggestionRange ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSuggestionClick.invoke(
                            suggestionRange.start,
                            suggestionRange.endInclusive
                        )
                    }
                    .background(
                        color = FluentlyTheme.colors.primaryContainerColor,
                        shape = FluentlyTheme.shapes.xxsmall
                    )
                    .padding(8.dp)
            ) {
                val startDateTimeString = suggestionRange.start.toDayMonthYearHoursMinutesAbbreviatedString()
                val endDateTimeString = suggestionRange.endInclusive.toDayMonthYearHoursMinutesAbbreviatedString()
                FluentlyText(
                    text = "$startDateTimeString - $endDateTimeString",
                    color = FluentlyTheme.colors.onPrimaryContainerColor,
                    style = FluentlyTheme.typography.caption3
                )
            }
            Spacer(Modifier.size(8.dp))
        }
    }
}

@Composable
private fun LocalDateTime.toDayMonthYearHoursMinutesAbbreviatedString(): String {
    val currentDateTime = this
    val currentDateString = currentDateTime.date.toDayMonthYearAbbreviatedString()
    val currentTimeString = currentDateTime.time.toHoursMinutesString()
    return "$currentDateString $currentTimeString"
}