package io.github.liukscot.sonari.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/* Spacing scale — ported from the design system (tokens/spacing.css, 4px grid)
   plus the two semantic tokens it uses for screen layout. Component-internal
   sizes (icon box, buttons, slider track/knob) stay with their component,
   matching how the design system defines them. */
@Immutable
data class SonariSpacing(
    val xs: Dp = 4.dp,         // sp-1
    val sm: Dp = 8.dp,         // sp-2
    val md: Dp = 12.dp,        // sp-3 / grid gap
    val lg: Dp = 16.dp,        // sp-4
    val xl: Dp = 24.dp,        // sp-6
    val cardPad: Dp = 14.dp,   // --card-pad (inside a sound card)
    val screenEdge: Dp = 18.dp,// screen horizontal padding
)

val LocalSonariSpacing = staticCompositionLocalOf { SonariSpacing() }
