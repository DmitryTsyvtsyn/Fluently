package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast
import kotlinx.collections.immutable.PersistentList

data class SettingsViewState(
    val contrast: ThemeContrast = ThemeContrast.LIGHT,
    val contrasts: PersistentList<ThemeContrast> = ThemeContrast.entriesWithDynamicIfPossible
)