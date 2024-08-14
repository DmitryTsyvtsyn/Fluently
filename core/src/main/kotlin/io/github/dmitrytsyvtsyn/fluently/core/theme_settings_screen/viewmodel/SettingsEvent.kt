package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast

sealed interface SettingsEvent {

    class ChangeContrast(val contrast: ThemeContrast) : SettingsEvent

}