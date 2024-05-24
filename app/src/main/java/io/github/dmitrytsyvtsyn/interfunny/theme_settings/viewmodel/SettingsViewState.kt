package io.github.dmitrytsyvtsyn.interfunny.theme_settings.viewmodel

import io.github.dmitrytsyvtsyn.interfunny.core.theme.ThemeContrast
import kotlinx.collections.immutable.PersistentList

data class SettingsViewState(
    val contrast: ThemeContrast,
    val contrasts: PersistentList<ThemeContrast>
)