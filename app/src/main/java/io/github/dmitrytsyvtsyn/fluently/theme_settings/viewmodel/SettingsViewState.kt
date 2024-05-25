package io.github.dmitrytsyvtsyn.fluently.theme_settings.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewState
import kotlinx.collections.immutable.PersistentList

data class SettingsViewState(
    val contrast: ThemeContrast,
    val contrasts: PersistentList<ThemeContrast>
) : ViewState