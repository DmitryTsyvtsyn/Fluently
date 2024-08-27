package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient

@Immutable
class Shapes(
    val xxsmall: Shape,
    val xsmall: Shape,
    val small: Shape,
    val medium: Shape,
    val big: Shape
) {
    companion object {
        val Default = Shapes(
            xxsmall = RoundedCornerShape(4.dp),
            xsmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            big = RoundedCornerShape(24.dp)
        )

        fun createFrom(coefficient: ThemeShapeCoefficient): Shapes {
            val factor = coefficient.value
            return Shapes(
                xxsmall = RoundedCornerShape(4.dp),
                xsmall = RoundedCornerShape((4 + (8 * factor)).dp), // from 4 to 12
                small = RoundedCornerShape((8 + (8 * factor)).dp), // from 8 to 16
                medium = RoundedCornerShape((12 + (12 * factor)).dp), // from 12 to 24
                big = RoundedCornerShape((16 + (16 * factor)).dp) // from 16 to 32
            )
        }
    }
}

internal val LocalShapes = staticCompositionLocalOf<Shapes> { error("Shapes is not provided!") }