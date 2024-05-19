package io.github.dmitrytsyvtsyn.interfunny.interview_event_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.dmitrytsyvtsyn.interfunny.R
import kotlin.math.roundToInt

@Composable
fun formatFloatingHours(hours: Float): String {
    if (hours < 1) {
        return stringResource(id = R.string.minute_suffix, (hours * 60).toInt())
    }

    val divider = (hours * 10).toInt() % 10
    if (divider == 0) {
        return stringResource(id = R.string.hour_suffix, hours.toInt())
    }

    if (divider == 5) {
        stringResource(id = R.string.minute_suffix, "${hours.toInt()}.5")
    }

    return stringResource(id = R.string.hour_suffix, hours.roundToInt())
}