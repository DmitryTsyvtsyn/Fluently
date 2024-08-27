package io.github.dmitrytsyvtsyn.fluently.core.theme.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme

@Composable
fun FluentlyCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = FluentlyTheme.colors.primaryColor,
        uncheckedColor = FluentlyTheme.colors.primaryColor,
        checkmarkColor = FluentlyTheme.colors.onPrimaryColor,
        disabledCheckedColor = FluentlyTheme.colors.primaryColor,
        disabledUncheckedColor = FluentlyTheme.colors.primaryColor,
        disabledIndeterminateColor = FluentlyTheme.colors.primaryColor
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}