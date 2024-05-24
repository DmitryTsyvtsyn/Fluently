package io.github.dmitrytsyvtsyn.interfunny.interview_list.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.interfunny.interview_list.formatFloatingHours
import io.github.dmitrytsyvtsyn.interfunny.interview_list.viewmodel.states.InterviewListItemState

private const val TIMELINE_SIZE_FACTOR = 24f
private const val ONE_HOUR_IN_MILLIS = 3_600f * 1_000

@Composable
fun TimelineListItem(
    textStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onBackground
    ),
    paddingHorizontal: Dp = 16.dp,
    textMargin: Dp = 8.dp,
    timeline: InterviewListItemState.Timeline
) {
    val density = LocalDensity.current.density
    val paddingHorizontalFloat = paddingHorizontal.value * density
    val textMarginFloat = textMargin.value * density

    val textMeasurer = rememberTextMeasurer()

    val hourFactor = (timeline.endDate - timeline.startDate) / ONE_HOUR_IN_MILLIS
    val hourString = formatFloatingHours(timeline.startDate, timeline.endDate)
    val textLayoutResult = remember { textMeasurer.measure(hourString, textStyle) }

    val shortPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f) }
    val longPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f) }

    val normalColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    Canvas(
        Modifier
            .fillMaxWidth()
            .height((hourFactor * TIMELINE_SIZE_FACTOR).dp)
    ) {
        val color = when {
            hourFactor < 2f -> errorColor
            else -> normalColor
        }

        drawLine(
            color = color,
            strokeWidth = 4f,
            start = Offset(paddingHorizontalFloat, 0f),
            end = Offset(paddingHorizontalFloat, size.height),
            pathEffect = if (hourFactor < 1.5f) shortPathEffect else longPathEffect
        )

        drawText(
            textMeasurer = textMeasurer,
            text = hourString,
            style = textStyle.copy(color = color),
            topLeft = Offset(
                x = paddingHorizontalFloat + textMarginFloat,
                y = size.height / 2f - textLayoutResult.size.height / 2,
            )
        )
    }
}