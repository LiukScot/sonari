package io.github.liukscot.sonari.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/* Twilight palette — ported from the design system (tokens/colors.css).
   Dark-only. Accent is the violet->pink gradient; on small details use the
   solid accent instead (gradients on small things hurt legibility). */

// ---- Accent ----
val Violet = Color(0xFF9B8CFF)       // gradient start
val Pink = Color(0xFFFF8FB1)         // gradient end
val AccentSolid = Color(0xFFC69BFF)  // text, icons, small details
val AccentSoft = Color(0xFFB9A8FF)   // hover lift on the solid accent

// ---- Dark surfaces ----
val InkBase = Color(0xFF050506)
val InkBg = Color(0xFF070708)
val Ink1 = Color(0xFF161619)
val Ink2 = Color(0xFF1B1B1F)
val Ink3 = Color(0xFF1E1E22)
val Ink4 = Color(0xFF26262B)

// ---- Text ----
val TextStrong = Color(0xFFF6F5FB)
val TextBody = Color(0xFFD9D7E3)
val TextMuted = Color(0xFF9D9BAB)
val TextFaint = Color(0xFF6C6A7A)
val TextOnAccent = Color(0xFF1A1430)  // dark ink on the bright gradient

// ---- Semantic (quiet, twilight-toned) ----
val Success = Color(0xFF7FD6A6)
val Warning = Color(0xFFF2C879)
val Danger = Color(0xFFFF8B8B)

/* The signature gradient. Offset.Infinite makes it run top-left -> bottom-right,
   approximating the design system's 135deg diagonal. Use ONLY on large elements. */
val AccentGradient = Brush.linearGradient(
    colors = listOf(Violet, Pink),
    start = Offset.Zero,
    end = Offset.Infinite,
)

// Active sound-card surface: subtle top-light (#1e1e22 -> #161619).
val SurfaceCardActive = Brush.verticalGradient(listOf(Ink3, Ink1))

/* Project color holder, à la MaterialTheme.colorScheme. Read via
   SonariTheme.colors.xxx inside composables. */
@Immutable
data class SonariColors(
    val surfaceApp: Color,
    val surfaceCard: Color,
    val surfaceRaised: Color,
    val surfaceHover: Color,
    val surfacePressed: Color,
    val accentSolid: Color,
    val accentGradient: Brush,
    val surfaceCardActive: Brush,
    val textStrong: Color,
    val textBody: Color,
    val textMuted: Color,
    val textFaint: Color,
    val textOnAccent: Color,
    val borderFaint: Color,
    val borderDefault: Color,
    val borderHover: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
)

val SonariDarkColors = SonariColors(
    surfaceApp = InkBg,
    surfaceCard = Ink1,
    surfaceRaised = Ink2,
    surfaceHover = Ink3,
    surfacePressed = Ink4,
    accentSolid = AccentSolid,
    accentGradient = AccentGradient,
    surfaceCardActive = SurfaceCardActive,
    textStrong = TextStrong,
    textBody = TextBody,
    textMuted = TextMuted,
    textFaint = TextFaint,
    textOnAccent = TextOnAccent,
    borderFaint = Color(0x0FFFFFFF),  // .06 white hairline divider (design system --line-1)
    borderDefault = Color.Transparent,
    borderHover = Color.Transparent,
    success = Success,
    warning = Warning,
    danger = Danger,
)

val LocalSonariColors = staticCompositionLocalOf { SonariDarkColors }
