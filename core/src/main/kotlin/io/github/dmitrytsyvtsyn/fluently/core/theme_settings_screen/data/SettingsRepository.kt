package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data

interface SettingsRepository {
    suspend fun saveThemeColorVariant(variant: Int): Boolean
    suspend fun readThemeColorVariant(default: Int): Int
}