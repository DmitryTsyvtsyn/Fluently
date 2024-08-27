package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.R
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIcon
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun ThemeSelectionColorsRow(
    themeColorVariant: ThemeColorVariant,
    themeColorVariants: PersistentList<ThemeColorVariant>,
    onClick: (ThemeColorVariant) -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        themeColorVariants.forEach { variant ->
            val colors = if (isSystemInDarkTheme()) variant.darkColors else variant.lightColors
            Box(
                modifier = Modifier
                    .clickable { onClick.invoke(variant) }
                    .size(56.dp)
                    .background(
                        color = colors.primaryColor,
                        shape = FluentlyTheme.shapes.small
                    )
            ) {
                if (themeColorVariant == variant) {
                    FluentlyIcon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.Center),
                        tint = FluentlyTheme.colors.onPrimaryColor
                    )
                }
            }
        }
    }
}