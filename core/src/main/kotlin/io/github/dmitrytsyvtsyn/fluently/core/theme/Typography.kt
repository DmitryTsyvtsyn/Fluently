package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf

class Typography

internal val LocalTypography = staticCompositionLocalOf<Typography> { error("Typography is not provided") }

val typography = Typography()