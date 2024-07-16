package io.github.dmitrytsyvtsyn.fluently.happening_detail.composables

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.happening_detail.R
import kotlinx.collections.immutable.PersistentList

@Composable
fun Suggestions(
    suggestionRanges: PersistentList<LongRange>,
    onSuggestionClick: (startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int) -> Unit
) {
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.try_folowing_ranges))
        append(" ")

        suggestionRanges.forEachIndexed { index, range ->
            pushStringAnnotation("range", "${range.first}/${range.last}")

            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                val trailingSymbol = if (index != suggestionRanges.lastIndex) "\n" else ""

                val now = System.currentTimeMillis()
                val startDate = range.first
                val endDate = range.last

                val startDateString = CalendarRepository.formatDateMonthYearBrief(startDate)
                val nowDateString = CalendarRepository.formatDateMonthYearBrief(now)
                val endDateString = CalendarRepository.formatDateMonthYearBrief(endDate)

                val dates = if (startDateString == endDateString) {
                    if (startDateString == nowDateString) {
                        stringResource(id = R.string.at_today)
                    } else {
                        stringResource(id = R.string.at_date, nowDateString)
                    }
                } else {
                    stringResource(id = R.string.at_dates, startDateString, endDateString)
                }

                stringResource(id = R.string.at_date, )
                stringResource(id = R.string.at_date)

                append("${index + 1}. ${CalendarRepository.formatHoursMinutes(startDate)} - ${CalendarRepository.formatHoursMinutes(endDate)} $dates $trailingSymbol")
            }

            pop()
        }
    }

    ClickableText(
        text = annotatedString,
        style = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 28.sp
        ),
    ) { offset ->
        annotatedString.getStringAnnotations(tag = "range", start = offset, end = offset).firstOrNull()?.let {
            val some = it.item.split("/")
            val startDate = CalendarRepository.formatHoursMinutes(some.first().toLong())
            val endDate = CalendarRepository.formatHoursMinutes(some.last().toLong())

            val startHoursAndMinutes = startDate.split(":")
            val endHoursAndMinutes = endDate.split(":")

            onSuggestionClick.invoke(
                startHoursAndMinutes.first().toInt(),
                startHoursAndMinutes.last().toInt(),
                endHoursAndMinutes.first().toInt(),
                endHoursAndMinutes.last().toInt()
            )
        }
    }
}