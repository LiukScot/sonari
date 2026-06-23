package io.github.liukscot.sonari.ui.mixer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.ui.theme.SonariTheme

/* Sleep-timer picker — a 3-column grid of presets, mirroring the design system
   mockup (ui_kits/sonari-app TimerSheet). The active preset shows the accent
   gradient; tapping one sets the timer and closes. 0 = off. No custom slider:
   the design system only specifies these fixed presets. */
private val OPTIONS = listOf(0, 15, 30, 45, 60, 120)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerSheet(
    currentMinutes: Int,
    onSelect: (minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceCard,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = spacing.screenEdge).padding(bottom = spacing.xl)) {
            Text(
                text = stringResource(R.string.sleep_timer),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textStrong,
            )
            Text(
                text = stringResource(R.string.timer_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.padding(top = spacing.xs, bottom = spacing.md),
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                OPTIONS.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        row.forEach { minutes ->
                            TimerOption(
                                minutes = minutes,
                                selected = currentMinutes == minutes,
                                modifier = Modifier.weight(1f),
                                onClick = { onSelect(minutes) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerOption(minutes: Int, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = SonariTheme.colors
    val shape = SonariTheme.shapes.md
    Box(
        modifier
            .height(52.dp)
            .clip(shape)
            .then(
                if (selected) Modifier.background(colors.accentGradient)
                else Modifier.background(colors.surfaceRaised),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = labelFor(minutes),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) colors.textOnAccent else colors.textBody,
        )
    }
}

@Composable
private fun labelFor(minutes: Int): String = when (minutes) {
    0 -> stringResource(R.string.timer_off)
    60 -> stringResource(R.string.timer_1_hour)
    120 -> stringResource(R.string.timer_2_hours)
    else -> stringResource(R.string.timer_minutes, minutes)
}
