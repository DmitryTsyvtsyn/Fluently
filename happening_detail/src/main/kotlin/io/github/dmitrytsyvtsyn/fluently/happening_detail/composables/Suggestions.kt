package io.github.dmitrytsyvtsyn.fluently.happening_detail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.happening_detail.R
import io.github.dmitrytsyvtsyn.fluently.happening_detail.toDayMonthYearAbbreviatedString
import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDateTime

@Composable
fun Suggestions(
    suggestionRanges: PersistentList<ClosedRange<LocalDateTime>>,
    onSuggestionClick: (startDateTime: LocalDateTime, endDateTime: LocalDateTime) -> Unit,
    maxSuggestionsShowed: Int = 3
) {
    Column {
        Text(
            text = stringResource(id = R.string.you_have_already_scheduled_events),
            color = MaterialTheme.colorScheme.error,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.size(12.dp))

        suggestionRanges.take(maxSuggestionsShowed).forEach { suggestionRange ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSuggestionClick.invoke(suggestionRange.start, suggestionRange.endInclusive)
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                val startDateTimeString = suggestionRange.start.toDayMonthYearHoursMinutesAbbreviatedString()
                val endDateTimeString = suggestionRange.endInclusive.toDayMonthYearHoursMinutesAbbreviatedString()
                Text(
                    text = "$startDateTimeString - $endDateTimeString",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.size(8.dp))
        }
    }
}

private fun LocalDateTime.toDayMonthYearHoursMinutesAbbreviatedString(): String {
    val currentDateTime = this
    val currentDateString = currentDateTime.date.toDayMonthYearAbbreviatedString()
    val currentTimeString = currentDateTime.time.toHoursMinutesString()
    return "$currentDateString $currentTimeString"
}