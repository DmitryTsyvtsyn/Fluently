package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class Colors(
    val primaryColor: Color = Color.Unspecified,
    val primaryContainerColor: Color,
    val secondaryColor: Color,
    val secondaryContainerColor: Color,
    val onPrimaryColor: Color,
    val backgroundColor: Color,
    val primaryTextColor: Color,
    val errorColor: Color
) {
    companion object {
        val Light = Colors(
            primaryColor = Color(0xFF426833),
            primaryContainerColor = Color(0xFFC3EFAD),
            secondaryColor = Color(0xFFA8D293),
            secondaryContainerColor = Color(0xFFE7E7E7),
            onPrimaryColor = Color(0xFFFFFFFF),
            backgroundColor = Color(0xFFF8FAF0),
            primaryTextColor = Color(0xFF000000),
            errorColor = Color(0xFFFFB4AB)
        )
        val Dark = Colors(
            primaryColor = Color(0xFF426833),
            primaryContainerColor = Color(0xFF2B4F1E),
            secondaryColor = Color(0xFF426833),
            secondaryContainerColor = Color(0xFF303030),
            onPrimaryColor = Color(0xFFFFFFFF),
            backgroundColor = Color(0xFF11140F),
            primaryTextColor = Color(0xFFFFFFFF),
            errorColor = Color(0xFFFFB4AB)
        )
    }
}

internal val LocalColors = staticCompositionLocalOf<Colors> { error("Colors is not provided!") }