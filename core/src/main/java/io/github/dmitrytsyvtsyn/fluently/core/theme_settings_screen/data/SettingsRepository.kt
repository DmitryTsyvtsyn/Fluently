package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data

import android.content.SharedPreferences
import android.os.Build
import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepository(private val preferences: SharedPreferences) {

    private val editor = preferences.edit()

    suspend fun saveContrast(contrast: ThemeContrast) = withContext(Dispatchers.Default) {
        editor.putInt(THEME_CONTRAST_KEY, contrast.ordinal).commit()
    }

    suspend fun readContrast() = withContext(Dispatchers.Default) {
        val defaultValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ThemeContrast.DYNAMIC.ordinal else ThemeContrast.LIGHT.ordinal
        ThemeContrast.entries[preferences.getInt(THEME_CONTRAST_KEY, defaultValue)]
    }

    companion object {
        private const val THEME_CONTRAST_KEY = "theme_contrast_key"
    }

}