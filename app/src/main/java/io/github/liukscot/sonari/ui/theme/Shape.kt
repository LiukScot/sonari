package io.github.liukscot.sonari.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/* Corner radii — ported from the design system (tokens/spacing.css).
   Nothing is sharp-cornered. pill = fully rounded for any height. */
@Immutable
data class SonariShapes(
    val xs: RoundedCornerShape = RoundedCornerShape(8.dp),
    val sm: RoundedCornerShape = RoundedCornerShape(10.dp),   // chips, inputs
    val md: RoundedCornerShape = RoundedCornerShape(14.dp),   // sound cards
    val lg: RoundedCornerShape = RoundedCornerShape(18.dp),   // sheets, dialogs
    val xl: RoundedCornerShape = RoundedCornerShape(24.dp),   // bottom bar
    val pill: RoundedCornerShape = RoundedCornerShape(percent = 50), // play, sliders
)

val LocalSonariShapes = staticCompositionLocalOf { SonariShapes() }
