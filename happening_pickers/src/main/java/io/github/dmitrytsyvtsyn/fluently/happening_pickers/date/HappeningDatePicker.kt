package io.github.dmitrytsyvtsyn.fluently.happening_pickers.date

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import io.github.dmitrytsyvtsyn.fluently.core.R
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.HappeningDatePickerDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HappeningDatePicker(
    params: HappeningDatePickerDestination.Params,
    dismiss: () -> Unit,
    apply: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = params.initialDate)
    val confirmEnabled = remember {
        derivedStateOf {
            val date = datePickerState.selectedDateMillis
            date != null && date >= params.minDate && date <= params.maxDate
        }
    }

    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    apply.invoke(datePickerState.selectedDateMillis ?: error("DatePickerState.selectedDateMillis is null!"))
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}