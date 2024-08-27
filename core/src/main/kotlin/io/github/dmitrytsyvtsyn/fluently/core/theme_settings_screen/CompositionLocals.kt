package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel.SettingsViewModel

val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> { error("Don't forget about me(") }