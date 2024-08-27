package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient

sealed interface SettingsEvent {
    data object Init : SettingsEvent
    class ChangeThemeColorVariant(val variant: ThemeColorVariant) : SettingsEvent
    class ChangeThemeShapeCoefficient(val coefficient: ThemeShapeCoefficient) : SettingsEvent
}