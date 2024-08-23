package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models

import androidx.compose.ui.graphics.Color
import io.github.dmitrytsyvtsyn.fluently.core.theme.Colors

enum class ThemeColorVariant(val lightColors: Colors, val darkColors: Colors) {
    DEFAULT(
        lightColors = Colors.Light,
        darkColors = Colors.Dark
    ),
    BLUE(
        lightColors = Colors.Light.copy(
            primaryColor = Color(0xff55679C)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xff55679C)
        )
    ),
    LIGHT_RED(
        lightColors = Colors.Light.copy(
            primaryColor = Color(0xffFF8A8A)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xffFF8A8A)
        )
    ),
    DARK_PURPLE(
        lightColors = Colors.Light.copy(
            primaryColor = Color(0xff2E073F)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xff2E073F)
        )
    ),
}