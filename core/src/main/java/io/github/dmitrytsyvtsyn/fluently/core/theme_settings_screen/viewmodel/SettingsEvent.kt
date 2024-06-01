package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast

sealed interface SettingsEvent {
    data class ChangeContrast(val contrast: ThemeContrast) : SettingsEvent
}