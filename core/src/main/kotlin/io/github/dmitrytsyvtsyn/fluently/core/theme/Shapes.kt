package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf

class Shapes

internal val LocalShapes = staticCompositionLocalOf<Shapes> { error("Shapes is not provided!") }

val shapes = Shapes()