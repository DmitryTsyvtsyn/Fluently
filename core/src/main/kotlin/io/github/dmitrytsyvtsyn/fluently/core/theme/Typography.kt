package io.github.dmitrytsyvtsyn.fluently.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.core.R

@Immutable
class Typography(
    val title1: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle,
    val caption3: TextStyle,
    val caption4: TextStyle,
)

internal val LocalTypography = staticCompositionLocalOf<Typography> { error("Typography is not provided") }

private val robotoFamily = FontFamily(
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_light, FontWeight.Light)
)

val typography = Typography(
    title1 = TextStyle(
        fontSize = 22.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal
    ),
    body1 = TextStyle(
        fontSize = 21.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal
    ),
    body2 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal
    ),
    body3 = TextStyle(
        fontSize = 27.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium
    ),
    body4 = TextStyle(
        fontSize = 15.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
    ),
    caption1 = TextStyle(
        fontSize = 16.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Bold
    ),
    caption2 = TextStyle(
        fontSize = 23.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium
    ),
    caption3 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium
    ),
    caption4 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal
    )
)