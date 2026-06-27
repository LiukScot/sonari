package io.github.liukscot.sonari.ui.presets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.audio.BUILT_IN_SOUNDS
import io.github.liukscot.sonari.audio.PRESET_ICON_OPTIONS
import io.github.liukscot.sonari.audio.Preset
import io.github.liukscot.sonari.audio.PresetStore
import io.github.liukscot.sonari.ui.soundIconRes
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme
import kotlinx.coroutines.launch

@Composable
fun PresetsScreen(engine: AudioEngine, modifier: Modifier = Modifier) {
    val colors = SonariTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by engine.state.collectAsState()
    val presets by PresetStore.presets(context).collectAsState(emptyList())
    var showNewDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Preset?>(null) }

    Column(modifier.fillMaxSize().background(colors.surfaceApp)) {
        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 8.dp)) {
            Text(
                text = stringResource(R.string.nav_presets),
                fontFamily = SonariSans,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textStrong,
                letterSpacing = (-0.02f).em,
            )
            Text(
                text = stringResource(R.string.presets_subtitle),
                fontFamily = SonariSans,
                fontSize = 12.sp,
                color = colors.textMuted,
                modifier = Modifier.padding(top = 3.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(presets, key = { it.id }) { preset ->
                val isPlaying = state.isPlaying && state.volumes == preset.volumes
                // Each preset = two grouped cards (main row + tile row), 2dp apart
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    PresetMainRow(
                        preset = preset,
                        isPlaying = isPlaying,
                        onTogglePlay = {
                            if (isPlaying) {
                                engine.pause()
                            } else {
                                if (state.isPlaying) engine.pause()
                                engine.loadMix(preset.volumes, preset.masterVolume)
                                engine.play()
                            }
                        },
                        onLongPress = { deleteTarget = preset },
                    )
                    PresetTileRow(
                        preset = preset,
                        onToggleTile = { enabled ->
                            scope.launch { PresetStore.setTileEnabled(context, preset.id, enabled) }
                        },
                        onLongPress = { deleteTarget = preset },
                    )
                }
            }

            item {
                NewPresetButton(
                    onClick = { showNewDialog = true },
                    enabled = state.volumes.isNotEmpty(),
                )
            }
        }
    }

    if (showNewDialog) {
        NewPresetDialog(
            onDismiss = { showNewDialog = false },
            onSave = { name, iconName ->
                scope.launch {
                    PresetStore.save(
                        context,
                        Preset(
                            name = name,
                            iconName = iconName,
                            volumes = state.volumes,
                            masterVolume = state.masterVolume,
                        ),
                    )
                }
                showNewDialog = false
            },
        )
    }

    deleteTarget?.let { preset ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = colors.surfaceCard,
            titleContentColor = colors.textStrong,
            textContentColor = colors.textMuted,
            title = { Text(stringResource(R.string.presets_delete_title)) },
            text = { Text(stringResource(R.string.presets_delete_body, preset.name)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { PresetStore.delete(context, preset.id) }
                    if (state.isPlaying && state.volumes == preset.volumes) engine.pause()
                    deleteTarget = null
                }) {
                    Text(stringResource(R.string.presets_delete_confirm), color = colors.accentSolid)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(R.string.presets_cancel), color = colors.textMuted)
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetMainRow(
    preset: Preset,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onLongPress: () -> Unit,
) {
    val colors = SonariTheme.colors
    val shapes = SonariTheme.shapes
    val shape = groupedShape(outer = 14.dp, isFirst = true, isLast = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (isPlaying) Modifier.background(brush = colors.surfaceCardActive)
                else Modifier.background(colors.surfaceCard)
            )
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(shapes.sm).background(colors.accentSolid.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(soundIconRes(preset.iconName)),
                contentDescription = null,
                tint = colors.accentSolid,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = preset.name,
                fontFamily = SonariSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textStrong,
            )
            Row(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                preset.volumes.keys.forEach { soundId ->
                    val iconName = BUILT_IN_SOUNDS.firstOrNull { it.id == soundId }?.iconName
                    if (iconName != null) {
                        Icon(
                            painter = painterResource(soundIconRes(iconName)),
                            contentDescription = null,
                            tint = colors.textFaint,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }

        Box(
            Modifier.size(40.dp).clip(shapes.pill).background(colors.surfaceRaised)
                .clickable(onClick = onTogglePlay),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = null,
                tint = colors.textStrong,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetTileRow(
    preset: Preset,
    onToggleTile: (Boolean) -> Unit,
    onLongPress: () -> Unit,
) {
    val colors = SonariTheme.colors
    val view = LocalView.current
    val shape = groupedShape(outer = 14.dp, isFirst = false, isLast = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceCard)
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_zap),
                contentDescription = null,
                tint = if (preset.tileEnabled) colors.accentSolid else colors.textFaint,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(R.string.presets_tile_toggle),
                fontFamily = SonariSans,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMuted,
            )
        }
        Switch(
            checked = preset.tileEnabled,
            onCheckedChange = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onToggleTile(it)
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = colors.accentSolid,
                checkedThumbColor = colors.surfaceApp,
            ),
        )
    }
}

private fun groupedShape(outer: Dp, isFirst: Boolean, isLast: Boolean): RoundedCornerShape {
    val top = if (isFirst) outer else 4.dp
    val bottom = if (isLast) outer else 4.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

@Composable
private fun NewPresetButton(onClick: () -> Unit, enabled: Boolean) {
    val colors = SonariTheme.colors
    val shapes = SonariTheme.shapes
    Box(
        Modifier.fillMaxWidth().clip(shapes.md)
            .background(colors.surfaceCard)
            .border(1.dp, if (enabled) colors.borderDefault else colors.borderFaint, shapes.md)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                tint = if (enabled) colors.accentSolid else colors.textFaint,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.presets_new_from_mix),
                fontFamily = SonariSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) colors.accentSolid else colors.textFaint,
            )
        }
    }
}

@Composable
private fun NewPresetDialog(onDismiss: () -> Unit, onSave: (name: String, iconName: String) -> Unit) {
    val colors = SonariTheme.colors
    val shapes = SonariTheme.shapes
    var name by rememberSaveable { mutableStateOf("") }
    var selectedIcon by rememberSaveable { mutableStateOf(PRESET_ICON_OPTIONS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceCard,
        titleContentColor = colors.textStrong,
        textContentColor = colors.textMuted,
        shape = shapes.lg,
        title = { Text(stringResource(R.string.presets_dialog_title), fontFamily = SonariSans) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.presets_dialog_name_hint),
                            color = colors.textFaint,
                            fontFamily = SonariSans,
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentSolid,
                        unfocusedBorderColor = colors.borderDefault,
                        focusedTextColor = colors.textStrong,
                        unfocusedTextColor = colors.textStrong,
                        cursorColor = colors.accentSolid,
                    ),
                )
                PRESET_ICON_OPTIONS.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { iconName ->
                            val selected = selectedIcon == iconName
                            Box(
                                Modifier.weight(1f).aspectRatio(1f).clip(shapes.sm)
                                    .background(if (selected) colors.accentSolid.copy(alpha = 0.16f) else colors.surfaceRaised)
                                    .border(1.dp, if (selected) colors.accentSolid else Color.Transparent, shapes.sm)
                                    .clickable { selectedIcon = iconName },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(soundIconRes(iconName)),
                                    contentDescription = null,
                                    tint = if (selected) colors.accentSolid else colors.textMuted,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSave(name.trim(), selectedIcon) },
                enabled = name.isNotBlank(),
            ) {
                Text(stringResource(R.string.presets_dialog_save), color = colors.accentSolid, fontFamily = SonariSans)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.presets_cancel), color = colors.textMuted, fontFamily = SonariSans)
            }
        },
    )
}
