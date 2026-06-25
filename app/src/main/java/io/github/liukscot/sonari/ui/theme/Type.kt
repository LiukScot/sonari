package io.github.liukscot.sonari.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.liukscot.sonari.R

/* Brand faces (OFL). Manrope is a single variable file: each weight asks the
   font's weight axis via FontVariation. The axis applies on API 26+; on API
   24-25 it falls back to the default instance (cosmetic only). JetBrains Mono
   ships as static weights for numerals. */
@OptIn(ExperimentalTextApi::class)
private fun manrope(weight: Int) = Font(
    R.font.manrope_variable,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val SonariSans = FontFamily(
    manrope(400), manrope(500), manrope(600), manrope(700), manrope(800),
)

val SonariMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, weight = FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, weight = FontWeight.Medium),
)

/* Small numeric readout (sound level, sleep-timer countdown) — JetBrains Mono
   at the design system's 11px caption size. */
val SonariMonoCaption = TextStyle(
    fontFamily = SonariMono,
    fontSize = 11.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.02.em,
)

/* Type scale — ported from the design system (tokens/typography.css).
   Titles use heavy weights + tight tracking; body stays regular. */
val SonariTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.4).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 23.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = SonariSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp,
    ),
)
