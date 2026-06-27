package io.github.liukscot.sonari.ui.mixer

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.liukscot.sonari.ui.theme.SonariTheme

/* Thin twilight slider, drawn directly: grey track + fill (accent gradient when
   active, faint otherwise) + white knob. Canvas avoids the layout/offset quirks
   of stacking Boxes and always uses the real drawn width. Sizes match the design
   system: card slider = 6/14, master VolumeSlider = 8/18. */
@Composable
fun SonariSlider(
    value: Float,            // 0f..1f
    active: Boolean,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 6.dp,
    knobSize: Dp = 14.dp,
) {
    val colors = SonariTheme.colors
    val v = value.coerceIn(0f, 1f)
    val view = LocalView.current
    val lastHapticMs = remember { LongArray(1) }

    Canvas(
        modifier
            .fillMaxWidth()
            .height(knobSize)
            .progressSemantics(v)
            .pointerInput(Unit) {
                detectTapGestures { if (size.width > 0) onValueChange((it.x / size.width).coerceIn(0f, 1f)) }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    if (size.width > 0) {
                        onValueChange((change.position.x / size.width).coerceIn(0f, 1f))
                        val now = System.currentTimeMillis()
                        if (now - lastHapticMs[0] >= 100L) {
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            lastHapticMs[0] = now
                        }
                    }
                }
            },
    ) {
        val cy = size.height / 2f
        val th = trackHeight.toPx()
        val kr = knobSize.toPx() / 2f
        val corner = CornerRadius(th / 2f)
        val top = Offset(0f, cy - th / 2f)
        // knob centre stays inside the track ends
        val knobCx = kr + (size.width - 2f * kr) * v

        drawRoundRect(color = colors.surfacePressed, topLeft = top, size = Size(size.width, th), cornerRadius = corner)
        if (v > 0f) {
            if (active) {
                drawRoundRect(brush = colors.accentGradient, topLeft = top, size = Size(knobCx, th), cornerRadius = corner)
            } else {
                drawRoundRect(color = colors.textFaint, topLeft = top, size = Size(knobCx, th), cornerRadius = corner)
            }
        }
        drawCircle(color = Color.White, radius = kr, center = Offset(knobCx, cy), alpha = if (active) 1f else 0.4f)
    }
}
