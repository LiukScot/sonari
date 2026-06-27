package io.github.liukscot.sonari.ui.mixer

import android.view.HapticFeedbackConstants
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.ui.theme.SonariMonoCaption
import io.github.liukscot.sonari.ui.theme.SonariTheme

/* Sound tile, Google-Home style: the whole box is the slider. The accent
   gradient fills left->right to the current volume; tap toggles on/off,
   horizontal drag adjusts the level relative to where it started. No glow /
   no border. clickable + draggable are the high-level gestures that coexist
   with the grid's vertical scroll. */
@Composable
fun SoundCard(
    @StringRes nameRes: Int,
    @DrawableRes iconRes: Int,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val active = volume > 0f
    val v = volume.coerceIn(0f, 1f)

    val view = LocalView.current
    var widthPx by remember { mutableIntStateOf(0) }
    var dragStart by remember { mutableFloatStateOf(0f) }   // volume when the drag began
    var dragAccum by remember { mutableFloatStateOf(0f) }   // accumulated px delta
    val lastStep = remember { IntArray(1) { Int.MIN_VALUE } }
    val latestVolume by rememberUpdatedState(v)
    val latestChange by rememberUpdatedState(onVolumeChange)
    val dragState = rememberDraggableState { deltaPx ->
        if (widthPx > 0) {
            dragAccum += deltaPx
            val newVal = (dragStart + dragAccum / widthPx).coerceIn(0f, 1f)
            latestChange(newVal)
            val step = (newVal * 100).toInt()
            if (step != lastStep[0]) {
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                lastStep[0] = step
            }
        }
    }

    Box(
        modifier
            .clip(SonariTheme.shapes.md)
            .background(colors.surfaceCard)
            .drawBehind {
                if (v > 0f) drawRect(brush = colors.accentGradient, size = Size(size.width * v, size.height))
            }
            .onSizeChanged { widthPx = it.width }
            .draggable(
                state = dragState,
                orientation = Orientation.Horizontal,
                onDragStarted = { dragStart = latestVolume; dragAccum = 0f; lastStep[0] = (latestVolume * 100).toInt() },
            )
            .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onToggle() }
            .heightIn(min = 116.dp)
            .padding(spacing.cardPad),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconChip(iconRes, active)
                Text(
                    text = if (active) {
                    String.format(java.util.Locale.getDefault(), "%d", (volume * 100).toInt())
                } else {
                    stringResource(R.string.sound_off)
                },
                    style = SonariMonoCaption,
                    color = if (active) colors.textStrong else colors.textFaint,
                )
            }
            Text(
                text = stringResource(nameRes),
                style = MaterialTheme.typography.labelLarge,
                color = if (active) colors.textStrong else colors.textBody,
            )
        }
    }
}

@Composable
private fun IconChip(@DrawableRes iconRes: Int, active: Boolean) {
    val colors = SonariTheme.colors
    Box(
        Modifier
            .size(42.dp)
            .clip(SonariTheme.shapes.sm)
            .background(if (active) Color.White.copy(alpha = 0.18f) else colors.surfaceRaised),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (active) colors.textStrong else colors.textMuted,
            modifier = Modifier.size(22.dp),
        )
    }
}
