package io.github.dmitrytsyvtsyn.fluently.core.theme.composables

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme

@Composable
fun FluentlyIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = FluentlyTheme.colors.primaryTextColor
    )
}