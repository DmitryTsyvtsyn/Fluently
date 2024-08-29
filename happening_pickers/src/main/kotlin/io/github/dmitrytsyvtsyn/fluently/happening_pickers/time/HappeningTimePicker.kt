package io.github.dmitrytsyvtsyn.fluently.happening_pickers.time

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyTextButton
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.HappeningTimePickerDestination
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.R
import io.github.dmitrytsyvtsyn.fluently.core.R as CoreRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HappeningTimePicker(
    params: HappeningTimePickerDestination.Params,
    dismiss: () -> Unit,
    apply: (startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int) -> Unit,
) {
    val startTimeState = rememberTimePickerState(
        initialHour = params.startHours,
        initialMinute = params.startMinutes,
        is24Hour = true
    )
    val endTimeState = rememberTimePickerState(
        initialHour = params.endHours,
        initialMinute = params.endMinutes,
        is24Hour = true
    )
    val confirmEnabled = remember {
        derivedStateOf {
            val nowDateTime = DateTimeExtensions.nowDateTime()
            val nowDateInMillis = nowDateTime.date.toEpochMillis()
            val nowTimeInMillis = nowDateTime.time.toMillisecondOfDay()
            when {
                nowDateInMillis > params.dateInMillis -> false
                nowDateInMillis == params.dateInMillis -> startTimeState.toMillis() > nowTimeInMillis
                else -> true
            }
        }
    }

    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            FluentlyTextButton(
                onClick = {
                    apply.invoke(
                        startTimeState.hour,
                        startTimeState.minute,
                        endTimeState.hour,
                        endTimeState.minute
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(id = CoreRes.string.ok))
            }
        },
        dismissButton = {
            FluentlyTextButton(onClick = dismiss) {
                Text(stringResource(id = CoreRes.string.cancel))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = FluentlyTheme.colors.backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            val focusRequester = remember { FocusRequester() }

            FluentlyText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.start_time),
                style = FluentlyTheme.typography.body2
            )

            Spacer(modifier = Modifier.size(16.dp))

            FluentlyTimeInput(
                state = startTimeState,
                modifier = Modifier.align(Alignment.CenterHorizontally).focusRequester(focusRequester),
            )

            Spacer(modifier = Modifier.size(16.dp))

            FluentlyText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.end_time),
                style = FluentlyTheme.typography.body2
            )

            Spacer(modifier = Modifier.size(16.dp))

            FluentlyTimeInput(
                state = endTimeState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FluentlyTimeInput(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    colors: TimePickerColors = TimePickerDefaults.colors(
        timeSelectorSelectedContentColor = FluentlyTheme.colors.primaryTextColor,
        timeSelectorSelectedContainerColor = FluentlyTheme.colors.secondaryColor,
        timeSelectorUnselectedContentColor = FluentlyTheme.colors.primaryTextColor,
        timeSelectorUnselectedContainerColor = FluentlyTheme.colors.secondaryColor,
        periodSelectorBorderColor = FluentlyTheme.colors.primaryTextColor,
        periodSelectorUnselectedContainerColor = FluentlyTheme.colors.primaryTextColor,
    )
) {
    TimeInput(
        state = state,
        modifier = modifier,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun TimePickerState.toMillis(): Int {
    val minutes = 60 * hour + minute
    return minutes * 60_000
}