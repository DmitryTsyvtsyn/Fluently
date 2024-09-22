package io.github.dmitrytsyvtsyn.fluently.happening_pickers.time.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FluentlyTimeInput(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    colors: TimePickerColors = TimePickerDefaults.colors(
        timeSelectorSelectedContentColor = FluentlyTheme.colors.onPrimaryContainerColor,
        timeSelectorSelectedContainerColor = FluentlyTheme.colors.primaryContainerColor,
        timeSelectorUnselectedContentColor = FluentlyTheme.colors.onPrimaryContainerColor,
        timeSelectorUnselectedContainerColor = FluentlyTheme.colors.primaryContainerColor,
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