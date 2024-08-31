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
            primaryColor = Color(0xFF55679C),
            primaryContainerColor = Color(0xFF879FE6),
            secondaryColor = Color(0xFF879FE6),
            backgroundColor = Color(0xFFEDF0F7),
            secondaryContainerColor = Color(0xFFDDDDDD)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xFF879FE6),
            primaryContainerColor = Color(0xFF55679C),
            secondaryColor = Color(0xFF879FE6),
            onPrimaryColor = Color(0xFF55679C),
            onPrimaryContainerColor = Color(0xFF879FE6)
        )
    ),
    LIGHT_RED(
        lightColors = Colors.Light.copy(
            primaryColor = Color(0xFFFF8A8A),
            primaryContainerColor = Color(0xFFF0BBBB),
            secondaryColor = Color(0xFFF0BBBB),
            backgroundColor = Color(0xFFFFF7F7)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xFFFF8A8A),
            primaryContainerColor = Color(0xFFB54C4C),
            secondaryColor = Color(0xFFFF8A8A),
            onPrimaryColor = Color(0xFFB54C4C),
            onPrimaryContainerColor = Color(0xFFFF8A8A)
        )
    ),
    DARK_PURPLE(
        lightColors = Colors.Light.copy(
            primaryColor = Color(0xFF66118C),
            primaryContainerColor = Color(0xFFb921fc),
            secondaryColor = Color(0xFFb921fc),
            backgroundColor = Color(0xFFf4edf7),
            secondaryContainerColor = Color(0xFFDDDDDD)
        ),
        darkColors = Colors.Dark.copy(
            primaryColor = Color(0xFFb921fc),
            primaryContainerColor = Color(0xFF560E75),
            secondaryColor = Color(0xFFb921fc),
            onPrimaryColor = Color(0xFF560E75),
            onPrimaryContainerColor = Color(0xFFb921fc)
        )
    ),
}