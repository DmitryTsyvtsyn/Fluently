package io.github.dmitrytsyvtsyn.fluently.core.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient

object FluentlyTheme {

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

}

@SuppressLint("NewApi")
@Composable
fun FluentlyTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    themeColorVariant: ThemeColorVariant,
    themeShapeCoefficient: ThemeShapeCoefficient,
    content: @Composable () -> Unit
) {
    val colors = if (isDark) themeColorVariant.darkColors else themeColorVariant.lightColors
    val shapes = Shapes.createFrom(themeShapeCoefficient)

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalShapes provides shapes,
        LocalTypography provides typography,
        content = content
    )
}