package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

class Shapes(
    val xxsmall: Shape,
    val xsmall: Shape,
    val small: Shape,
    val medium: Shape,
    val big: Shape
)

internal val LocalShapes = staticCompositionLocalOf<Shapes> { error("Shapes is not provided!") }

val shapes = Shapes(
    xxsmall = RoundedCornerShape(4.dp),
    xsmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    big = RoundedCornerShape(24.dp)
)