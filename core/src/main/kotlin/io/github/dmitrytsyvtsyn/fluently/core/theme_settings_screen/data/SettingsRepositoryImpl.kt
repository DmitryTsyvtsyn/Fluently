package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data

import android.content.SharedPreferences

internal class SettingsRepositoryImpl(private val preferences: SharedPreferences): SettingsRepository {

    private val editor = preferences.edit()

    override suspend fun saveThemeColorVariant(variant: Int): Boolean {
        return editor.putInt(THEME_COLOR_VARIANT_KEY, variant).commit()
    }

    override suspend fun readThemeColorVariant(default: Int): Int {
        return preferences.getInt(THEME_COLOR_VARIANT_KEY, default)
    }

    companion object {
        private const val THEME_COLOR_VARIANT_KEY = "theme_color_variant_key"
    }

}