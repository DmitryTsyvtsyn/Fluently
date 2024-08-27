package io.github.dmitrytsyvtsyn.fluently.happening_pickers.date

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import io.github.dmitrytsyvtsyn.fluently.core.R
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyTextButton
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.HappeningDatePickerDestination

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
            FluentlyTextButton(
                onClick = {
                    apply.invoke(datePickerState.selectedDateMillis ?: error("DatePickerState.selectedDateMillis is null!"))
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            FluentlyTextButton(onClick = dismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = FluentlyTheme.colors.backgroundColor
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = FluentlyTheme.colors.primaryTextColor,
                selectedDayContainerColor = FluentlyTheme.colors.primaryColor,
                selectedDayContentColor = FluentlyTheme.colors.onPrimaryColor,
                selectedYearContainerColor = FluentlyTheme.colors.primaryColor,
                selectedYearContentColor = FluentlyTheme.colors.onPrimaryColor,
                headlineContentColor = FluentlyTheme.colors.primaryTextColor,
                subheadContentColor = FluentlyTheme.colors.primaryTextColor,
                dayContentColor = FluentlyTheme.colors.primaryTextColor,
                weekdayContentColor = FluentlyTheme.colors.primaryTextColor,
                yearContentColor = FluentlyTheme.colors.primaryTextColor,
                todayContentColor = FluentlyTheme.colors.primaryColor,
                todayDateBorderColor = FluentlyTheme.colors.primaryColor,
                dateTextFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = FluentlyTheme.colors.primaryTextColor,
                    unfocusedTextColor = FluentlyTheme.colors.primaryTextColor,
                    focusedIndicatorColor = FluentlyTheme.colors.primaryColor,
                    unfocusedIndicatorColor = FluentlyTheme.colors.primaryColor,
                    cursorColor = FluentlyTheme.colors.primaryColor,
                    focusedLabelColor = FluentlyTheme.colors.primaryColor,
                    unfocusedLabelColor = FluentlyTheme.colors.primaryColor,
                    errorCursorColor = FluentlyTheme.colors.errorColor,
                    errorIndicatorColor = FluentlyTheme.colors.errorColor,
                    errorPlaceholderColor = FluentlyTheme.colors.errorColor,
                    errorLabelColor = FluentlyTheme.colors.errorColor,
                ),
            ),
        )
    }
}