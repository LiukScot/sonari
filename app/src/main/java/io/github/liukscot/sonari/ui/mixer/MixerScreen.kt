package io.github.liukscot.sonari.ui.mixer

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
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
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme

@Composable
fun MixerScreen(engine: AudioEngine, modifier: Modifier = Modifier) {
    val state by engine.state.collectAsState()
    val colors = SonariTheme.colors
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier.fillMaxSize().background(colors.surfaceApp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (landscape) 4 else 2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Scrolls away with the content (no pinned title).
            item(span = { GridItemSpan(maxLineSpan) }) { Header() }
            BUILT_IN_SOUNDS.groupBy { it.categoryRes }.forEach { (categoryRes, sounds) ->
                item(span = { GridItemSpan(maxLineSpan) }) { GroupHeader(categoryRes) }
                items(sounds, key = { it.id }) { sound ->
                    val volume = state.volumes[sound.id] ?: 0f
                    SoundCard(
                        nameRes = sound.nameRes,
                        iconRes = iconRes(sound.iconName),
                        volume = volume,
                        onVolumeChange = { engine.setVolume(sound.id, it) },
                        onToggle = { engine.toggle(sound.id) },
                    )
                }
            }
        }

        BottomBar(
            activeCount = state.volumes.size,
            isPlaying = state.isPlaying,
            masterVolume = state.masterVolume,
            onMasterChange = engine::setMasterVolume,
            onTogglePlay = engine::togglePlay,
        )
    }
}

@Composable
private fun BottomBar(
    activeCount: Int,
    isPlaying: Boolean,
    masterVolume: Float,
    onMasterChange: (Float) -> Unit,
    onTogglePlay: () -> Unit,
) {
    val colors = SonariTheme.colors
    val barShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    Column(
        Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = barShape, clip = false)
            .clip(barShape)
            .background(colors.surfaceCard),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.borderFaint))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (activeCount == 0) stringResource(R.string.no_sounds_yet)
                    else pluralStringResource(R.plurals.sounds_mixing, activeCount, activeCount),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textBody,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_volume_2),
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(20.dp),
                    )
                    SonariSlider(
                        value = masterVolume,
                        active = activeCount > 0,
                        onValueChange = onMasterChange,
                        modifier = Modifier.weight(1f),
                        trackHeight = 8.dp,
                        knobSize = 18.dp,
                    )
                }
            }

            // Raised icon button — sleep timer (wired in Phase 3); shown for parity.
            Box(
                Modifier
                    .size(62.dp)
                    .clip(SonariTheme.shapes.pill)
                    .background(colors.surfacePressed),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_moon),
                    contentDescription = stringResource(R.string.sleep_timer),
                    tint = colors.textStrong,
                    modifier = Modifier.size(24.dp),
                )
            }

            Box(
                Modifier
                    .size(62.dp)
                    .clip(SonariTheme.shapes.pill)
                    .background(colors.accentGradient)
                    .clickable(onClick = onTogglePlay),
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
    Row(
        Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
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
    Text(
        text = stringResource(titleRes).uppercase(),
        style = TextStyle(
            fontFamily = SonariSans,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.12.em,
        ),
        color = SonariTheme.colors.textFaint,
        modifier = Modifier.fillMaxWidth().padding(start = 2.dp, top = 8.dp, bottom = 2.dp),
    )
}

@DrawableRes
private fun iconRes(iconName: String): Int = when (iconName) {
    "cloud-rain" -> R.drawable.ic_cloud_rain
    "cloud-lightning" -> R.drawable.ic_cloud_lightning
    "wind" -> R.drawable.ic_wind
    "waves" -> R.drawable.ic_waves
    "droplet" -> R.drawable.ic_droplet
    "bird" -> R.drawable.ic_bird
    "moon-star" -> R.drawable.ic_moon_star
    "train-front" -> R.drawable.ic_train_front
    "sailboat" -> R.drawable.ic_sailboat
    "building-2" -> R.drawable.ic_building_2
    "coffee" -> R.drawable.ic_coffee
    "flame" -> R.drawable.ic_flame
    "audio-waveform" -> R.drawable.ic_audio_waveform
    "radio" -> R.drawable.ic_radio
    else -> error("No drawable mapped for icon '$iconName'")
}
