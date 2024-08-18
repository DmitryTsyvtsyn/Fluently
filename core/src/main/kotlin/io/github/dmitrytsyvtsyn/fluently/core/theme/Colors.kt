package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF426833)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC3EFAD)
val onPrimaryContainerLight = Color(0xFF042100)
val secondaryLight = Color(0xFF436833)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFC3EFAD)
val onSecondaryContainerLight = Color(0xFF052100)
val tertiaryLight = Color(0xFF386667)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFBCEBED)
val onTertiaryContainerLight = Color(0xFF002021)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFF8FAF0)
val onBackgroundLight = Color(0xFF191D17)
val surfaceLight = Color(0xFFF8FAF0)
val onSurfaceLight = Color(0xFF191D17)
val surfaceVariantLight = Color(0xFFDFE4D7)
val onSurfaceVariantLight = Color(0xFF43483F)
val outlineLight = Color(0xFF73796E)
val outlineVariantLight = Color(0xFFC3C8BB)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E312B)
val inverseOnSurfaceLight = Color(0xFFEFF2E8)
val inversePrimaryLight = Color(0xFFA8D293)

val primaryDark = Color(0xFFA8D293)
val onPrimaryDark = Color(0xFF153809)
val primaryContainerDark = Color(0xFF2B4F1E)
val onPrimaryContainerDark = Color(0xFFC3EFAD)
val secondaryDark = Color(0xFFA8D293)
val onSecondaryDark = Color(0xFF153809)
val secondaryContainerDark = Color(0xFF2B4F1E)
val onSecondaryContainerDark = Color(0xFFC3EFAD)
val tertiaryDark = Color(0xFFA0CFD1)
val onTertiaryDark = Color(0xFF003738)
val tertiaryContainerDark = Color(0xFF1E4E4F)
val onTertiaryContainerDark = Color(0xFFBCEBED)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF11140F)
val onBackgroundDark = Color(0xFFE1E4DA)
val surfaceDark = Color(0xFF11140F)
val onSurfaceDark = Color(0xFFE1E4DA)
val surfaceVariantDark = Color(0xFF43483F)
val onSurfaceVariantDark = Color(0xFFC3C8BB)
val outlineDark = Color(0xFF8D9387)
val outlineVariantDark = Color(0xFF43483F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE1E4DA)
val inverseOnSurfaceDark = Color(0xFF2E312B)
val inversePrimaryDark = Color(0xFF426833)

data class Colors(
    val primaryColor: Color,
    val primaryContainerColor: Color,
    val inversePrimaryColor: Color,
    val onPrimaryColor: Color,
    val secondaryColor: Color,
    val onSecondaryColor: Color,
    val backgroundColor: Color,
    val primaryTextColor: Color,
    val errorColor: Color
)

internal val LocalColors = staticCompositionLocalOf<Colors> { error("Colors is not provided!") }

val lightColors = Colors(
    primaryColor = primaryLight,
    primaryContainerColor = primaryContainerLight,
    inversePrimaryColor = inversePrimaryLight,
    onPrimaryColor = onPrimaryLight,
    secondaryColor = secondaryLight,
    onSecondaryColor = onSecondaryLight,
    backgroundColor = backgroundLight,
    primaryTextColor = Color.Black,
    errorColor = errorLight
)

val darkColors = Colors(
    primaryColor = primaryDark,
    primaryContainerColor = primaryContainerDark,
    inversePrimaryColor = inversePrimaryDark,
    onPrimaryColor = onPrimaryDark,
    secondaryColor = secondaryDark,
    onSecondaryColor = onSecondaryDark,
    backgroundColor = backgroundDark,
    primaryTextColor = Color.White,
    errorColor = errorDark
)
