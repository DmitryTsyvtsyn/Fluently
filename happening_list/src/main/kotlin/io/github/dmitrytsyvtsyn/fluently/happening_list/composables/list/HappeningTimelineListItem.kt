package io.github.dmitrytsyvtsyn.fluently.happening_list.composables.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.happening_list.composables.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState

private const val TIMELINE_SIZE_FACTOR = 24f

@Composable
internal fun HappeningTimelineListItem(
    textStyle: TextStyle = FluentlyTheme.typography.body4,
    paddingHorizontal: Dp = 16.dp,
    textMargin: Dp = 8.dp,
    timeline: HappeningListItemState.Timeline
) {
    val density = LocalDensity.current.density
    val paddingHorizontalFloat = paddingHorizontal.value * density
    val textMarginFloat = textMargin.value * density

    val textMeasurer = rememberTextMeasurer()

    val period = timeline.period

    val hourFactor = period.hours + period.minutes / 60f
    val hourString = period.toHoursMinutesString()

    val textLayoutResult = remember { textMeasurer.measure(hourString, textStyle) }
    val minHeight = (textLayoutResult.size.height / density).dp

    val shortPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f) }
    val longPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f) }

    val normalColor = FluentlyTheme.colors.primaryColor
    val errorColor = FluentlyTheme.colors.errorColor
    val canvasHeight = max(minHeight, (hourFactor * TIMELINE_SIZE_FACTOR).dp)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(canvasHeight)
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