package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data

import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast

interface SettingsRepository {
    suspend fun saveContrast(contrast: ThemeContrast): Boolean
    suspend fun readContrast(): ThemeContrast

}

