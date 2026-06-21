package io.github.liukscot.sonari.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* Brand faces. The design system proposes Manrope (UI) + JetBrains Mono
   (numerals). Those OFL files are not bundled yet, so we fall back to the
   system sans/mono — swap these two vals once res/font is populated and the
   rest of the scale stays unchanged. */
val SonariSans: FontFamily = FontFamily.Default
val SonariMono: FontFamily = FontFamily.Monospace

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
