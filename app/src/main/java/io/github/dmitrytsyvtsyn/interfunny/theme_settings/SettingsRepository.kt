package io.github.dmitrytsyvtsyn.interfunny.theme_settings

import android.content.SharedPreferences
import android.os.Build
import io.github.dmitrytsyvtsyn.interfunny.core.theme.ThemeContrast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepository(private val preferences: SharedPreferences) {

    private val editor = preferences.edit()

    suspend fun saveContrast(contrast: ThemeContrast) = withContext(Dispatchers.Default) {
        editor.putInt(theme_contrast_key, contrast.ordinal).commit()
    }

    suspend fun readContrast() = withContext(Dispatchers.Default) {
        val defaultValue =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ThemeContrast.DYNAMIC.ordinal else ThemeContrast.LIGHT.ordinal
        ThemeContrast.entries[preferences.getInt(theme_contrast_key, defaultValue)]
    }

    companion object {
        private const val theme_contrast_key = "theme_contrast_key"
    }

}