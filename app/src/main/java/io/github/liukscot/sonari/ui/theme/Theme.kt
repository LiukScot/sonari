package io.github.liukscot.sonari.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/* Single dark "twilight" theme — no switcher (see PLAN.md). This M3 scheme is
   only what stock Material components (sliders, ripples) read; the app's own
   surfaces/text come from SonariTheme.colors. */
private val SonariColorScheme = darkColorScheme(
    primary = AccentSolid,
    onPrimary = TextOnAccent,
    secondary = Violet,
    onSecondary = TextOnAccent,
    background = InkBg,
    onBackground = TextBody,
    surface = Ink1,
    onSurface = TextBody,
    surfaceVariant = Ink2,
    onSurfaceVariant = TextMuted,
    outline = TextFaint,
    error = Danger,
    onError = TextOnAccent,
)

@Composable
fun SonariTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSonariColors provides SonariDarkColors,
        LocalSonariShapes provides SonariShapes(),
        LocalSonariSpacing provides SonariSpacing(),
    ) {
        MaterialTheme(
            colorScheme = SonariColorScheme,
            typography = SonariTypography,
            content = content,
        )
    }
}

/* Accessor object so call sites read SonariTheme.colors / .shapes, mirroring
   MaterialTheme.colorScheme. (A function and an object may share a name.) */
object SonariTheme {
    val colors: SonariColors
        @Composable get() = LocalSonariColors.current
    val shapes: SonariShapes
        @Composable get() = LocalSonariShapes.current
    val spacing: SonariSpacing
        @Composable get() = LocalSonariSpacing.current
}
