package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.ThemeSettingsScreen

fun NavGraphBuilder.coreDestinations() {
    composable(
        route = ThemeSettingsDestination.route
    ) {
        ThemeSettingsScreen()
    }
}