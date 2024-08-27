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

    override suspend fun saveThemeShapeCoefficient(coefficient: Float): Boolean {
        return editor.putFloat(THEME_SHAPE_COEFFICIENT_KEY, coefficient).commit()
    }

    override suspend fun readThemeShapeCoefficient(default: Float): Float {
        return preferences.getFloat(THEME_SHAPE_COEFFICIENT_KEY, default)
    }

    companion object {
        private const val THEME_COLOR_VARIANT_KEY = "theme_color_variant_key"
        private const val THEME_SHAPE_COEFFICIENT_KEY = "theme_shape_coefficient_key"
    }

}