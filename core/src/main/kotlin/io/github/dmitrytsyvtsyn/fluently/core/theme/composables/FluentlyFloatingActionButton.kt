package io.github.dmitrytsyvtsyn.fluently.core.theme.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme

@Composable
fun FluentlyFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        elevation = elevation,
        interactionSource = interactionSource,
        shape = FluentlyTheme.shapes.big,
        containerColor = FluentlyTheme.colors.primaryColor,
        contentColor = FluentlyTheme.colors.onPrimaryColor,
        content = content
    )
}