package io.github.dmitrytsyvtsyn.fluently.theme_settings.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewEvent

sealed interface SettingsEvent : ViewEvent {
    data class ChangeContrast(val contrast: ThemeContrast) : SettingsEvent
}