package io.github.liukscot.sonari.ui.mixer

import android.content.res.Configuration
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.audio.BUILT_IN_SOUNDS
import io.github.liukscot.sonari.audio.MixController
import io.github.liukscot.sonari.audio.SonariPlayback
import kotlinx.coroutines.delay
import io.github.liukscot.sonari.ui.soundIconRes
import io.github.liukscot.sonari.ui.theme.SonariMonoCaption
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme

@Composable
fun MixerScreen(engine: AudioEngine, modifier: Modifier = Modifier) {
    val state by engine.state.collectAsState()
    val context = LocalContext.current
    val timer = remember(context) { SonariPlayback.sleepTimer(context) }
    val timerMinutes by timer.minutes.collectAsState()
    val timerEnd by timer.endRealtimeMs.collectAsState()
    var showTimerSheet by remember { mutableStateOf(false) }
    // Tick once a second so the countdown in the playback bar visibly runs.
    var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(timerEnd) {
        if (timerEnd != null) while (true) {
            now = SystemClock.elapsedRealtime()
            delay(1_000)
        }
    }
    val timerLabel = timerEnd?.let { end ->
        val total = ((end - now).coerceAtLeast(0L)) / 1000
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier.fillMaxSize().background(colors.surfaceApp)) {
        Box(Modifier.weight(1f)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (landscape) 4 else 2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = spacing.screenEdge,
                end = spacing.screenEdge,
                top = spacing.xs,
                bottom = spacing.screenEdge,
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Scrolls away with the content (no pinned title).
            item(span = { GridItemSpan(maxLineSpan) }) { Header() }
            BUILT_IN_SOUNDS.groupBy { it.categoryRes }.forEach { (categoryRes, sounds) ->
                item(span = { GridItemSpan(maxLineSpan) }) { GroupHeader(categoryRes) }
                items(sounds, key = { it.id }) { sound ->
                    val volume = state.volumes[sound.id] ?: 0f
                    SoundCard(
                        nameRes = sound.nameRes,
                        iconRes = soundIconRes(sound.iconName),
                        volume = volume,
                        onVolumeChange = { engine.setVolume(sound.id, it) },
                        onToggle = { engine.toggle(sound.id) },
                    )
                }
            }
        }
        // Gradient scrim: transparent → surfaceApp, cast over bottom of grid
        Box(
            Modifier.fillMaxWidth().height(48.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(
                    colors.surfaceApp.copy(alpha = 0f), colors.surfaceApp.copy(alpha = 0.8f),
                )))
        )
        } // Box(weight 1f)

        BottomBar(
            activeCount = state.volumes.size,
            isPlaying = state.isPlaying,
            timerLabel = timerLabel,
            onTogglePlay = { MixController.togglePlay(context) },
            onTimerClick = { showTimerSheet = true },
        )
    }

    if (showTimerSheet) {
        SleepTimerSheet(
            currentMinutes = timerMinutes,
            onSelect = { minutes ->
                timer.set(minutes)
                showTimerSheet = false
            },
            onDismiss = { showTimerSheet = false },
        )
    }
}

@Composable
private fun BottomBar(
    activeCount: Int,
    isPlaying: Boolean,
    timerLabel: String?,
    onTogglePlay: () -> Unit,
    onTimerClick: () -> Unit,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val view = LocalView.current
    val barShape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 4.dp, bottomEnd = 4.dp)

    Column(
        Modifier
            .fillMaxWidth()
            .clip(barShape)
            .background(colors.surfaceCard),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = spacing.screenEdge, vertical = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (activeCount == 0) stringResource(R.string.no_sounds_yet)
                    else pluralStringResource(R.plurals.sounds_mixing, activeCount, activeCount),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textBody,
                )
                if (timerLabel != null) {
                    Text(text = timerLabel, style = SonariMonoCaption, color = colors.accentSolid)
                }
            }

            // Raised icon button — sleep timer. Accent tint when a timer runs.
            Box(
                Modifier
                    .size(BottomBarButtonSize)
                    .clip(SonariTheme.shapes.pill)
                    .background(colors.surfacePressed)
                    .clickable(onClick = onTimerClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(if (timerLabel != null) R.drawable.ic_timer else R.drawable.ic_moon),
                    contentDescription = stringResource(R.string.sleep_timer),
                    tint = if (timerLabel != null) colors.accentSolid else colors.textStrong,
                    modifier = Modifier.size(24.dp),
                )
            }

            Box(
                Modifier
                    .size(BottomBarButtonSize)
                    .clip(SonariTheme.shapes.pill)
                    .background(colors.accentGradient)
                    .clickable { view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); onTogglePlay() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play),
                    tint = colors.textOnAccent,
                    modifier = Modifier.size(25.dp),
                )
            }
        }
    }
}

@Composable
private fun Header() {
    val spacing = SonariTheme.spacing
    Row(
        Modifier.fillMaxWidth().padding(top = spacing.cardPad, bottom = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_sonari_mark),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = SonariTheme.colors.textStrong,
        )
    }
}

/* Section header, styled like the Settings page sections: small uppercase,
   wide tracking, faint. */
@Composable
private fun GroupHeader(@StringRes titleRes: Int) {
    val spacing = SonariTheme.spacing
    Text(
        text = stringResource(titleRes).uppercase(java.util.Locale.getDefault()),
        style = TextStyle(
            fontFamily = SonariSans,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.12.em,
        ),
        color = SonariTheme.colors.textFaint,
        modifier = Modifier.fillMaxWidth().padding(top = spacing.sm, bottom = spacing.xs),
    )
}

// Shared size for the two circular icon buttons in the bottom bar.
private val BottomBarButtonSize = 62.dp
