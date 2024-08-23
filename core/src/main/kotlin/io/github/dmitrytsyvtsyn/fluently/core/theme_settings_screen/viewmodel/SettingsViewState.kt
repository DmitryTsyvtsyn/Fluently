package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import kotlinx.collections.immutable.PersistentList

data class SettingsViewState(
    val themeColorVariant: ThemeColorVariant,
    val themeColorVariants: PersistentList<ThemeColorVariant>
)