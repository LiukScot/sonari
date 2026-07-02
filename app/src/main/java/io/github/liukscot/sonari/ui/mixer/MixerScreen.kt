package io.github.liukscot.sonari.ui.mixer

import android.content.res.Configuration
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.AppPrefs
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.audio.BUILT_IN_SOUNDS
import io.github.liukscot.sonari.audio.MixController
import io.github.liukscot.sonari.audio.SonariPlayback
import io.github.liukscot.sonari.audio.SoundGroup
import io.github.liukscot.sonari.audio.defaultSoundGroups
import io.github.liukscot.sonari.audio.withGroupDeleted
import io.github.liukscot.sonari.ui.soundIconRes
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

internal sealed class GridItem {
    data class Header(val group: SoundGroup) : GridItem()
    data class Sound(val soundId: String, val groupId: String) : GridItem()
    val key: Any get() = when (this) {
        is Header -> "h_${group.id}"
        is Sound -> soundId
    }
}

internal fun groupsToFlat(groups: List<SoundGroup>): List<GridItem> = buildList {
    groups.forEach { g ->
        add(GridItem.Header(g))
        g.soundIds.forEach { soundId ->
            if (BUILT_IN_SOUNDS.any { it.id == soundId }) {
                add(GridItem.Sound(soundId, g.id))
            }
        }
    }
}

internal fun flatToGroups(flat: List<GridItem>): List<SoundGroup> {
    val result = mutableListOf<SoundGroup>()
    var current: SoundGroup? = null
    val sounds = mutableListOf<String>()
    for (item in flat) {
        when (item) {
            is GridItem.Header -> {
                current?.let { result.add(it.copy(soundIds = sounds.toList())) }
                current = item.group
                sounds.clear()
            }
            is GridItem.Sound -> sounds.add(item.soundId)
        }
    }
    current?.let { result.add(it.copy(soundIds = sounds.toList())) }
    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MixerScreen(engine: AudioEngine, modifier: Modifier = Modifier) {
    val state by engine.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    val timer = remember(context) { SonariPlayback.sleepTimer(context) }
    val timerMinutes by timer.minutes.collectAsState()
    val timerEnd by timer.endRealtimeMs.collectAsState()
    var showTimerSheet by remember { mutableStateOf(false) }
    var now by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(timerEnd) {
        if (timerEnd != null) while (true) { now = SystemClock.elapsedRealtime(); delay(1_000) }
    }
    val timerLabel = timerEnd?.let { end ->
        val total = ((end - now).coerceAtLeast(0L)) / 1000
        val h = total / 3600; val m = (total % 3600) / 60; val s = total % 60
        if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }

    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val defaultGroups = remember(context) { defaultSoundGroups(context) }
    var flat by remember { mutableStateOf(groupsToFlat(defaultGroups)) }
    var snapshot by remember { mutableStateOf(groupsToFlat(defaultGroups)) }
    LaunchedEffect(Unit) {
        val saved = AppPrefs.soundGroups(context).first() ?: defaultGroups
        flat = groupsToFlat(saved)
        snapshot = flat
    }
    val groups = remember(flat) { flatToGroups(flat) }
    val newGroupLabel = stringResource(R.string.edit_new_group)

    var editMode by remember { mutableStateOf(false) }
    var renamingGroup by remember { mutableStateOf<SoundGroup?>(null) }
    var deletingGroup by remember { mutableStateOf<SoundGroup?>(null) }
    var draggingGroupId by remember { mutableStateOf<String?>(null) }

    val lazyGridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        val fi = from.index - 1  // -1 for the Sonari logo item at grid index 0
        val ti = to.index - 1
        if (fi < 0 || ti < 0 || fi >= flat.size || ti >= flat.size || fi == ti) return@rememberReorderableLazyGridState
        when {
            flat[fi] is GridItem.Sound -> {
                // Reject moves that would place a sound before the first header
                val firstHeaderIdx = flat.indexOfFirst { it is GridItem.Header }
                if (firstHeaderIdx >= 0 && ti < firstHeaderIdx) return@rememberReorderableLazyGridState
                flat = flat.toMutableList().apply { add(ti, removeAt(fi)) }
            }
            flat[fi] is GridItem.Header -> {
                var groupEnd = flat.size
                for (i in fi + 1 until flat.size) {
                    if (flat[i] is GridItem.Header) { groupEnd = i; break }
                }
                val groupItems = flat.subList(fi, groupEnd).toList()
                if (ti < fi) {
                    var insertAt = ti
                    while (insertAt > 0 && flat[insertAt] !is GridItem.Header) insertAt--
                    val newFlat = flat.toMutableList()
                    newFlat.subList(fi, groupEnd).clear()
                    newFlat.addAll(insertAt, groupItems)
                    flat = newFlat
                } else if (ti >= groupEnd) {
                    val reduced = flat.toMutableList().apply { subList(fi, groupEnd).clear() }
                    val adjustedTi = ti - groupItems.size
                    var insertAt = reduced.size
                    for (i in adjustedTi + 1 until reduced.size) {
                        if (reduced[i] is GridItem.Header) { insertAt = i; break }
                    }
                    reduced.addAll(insertAt, groupItems)
                    flat = reduced
                }
            }
        }
    }

    Box(modifier.fillMaxSize().background(colors.surfaceApp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (landscape) 4 else 2),
            state = lazyGridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = spacing.screenEdge, end = spacing.screenEdge,
                top = spacing.xs, bottom = 96.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { Header() }

            items(flat, key = { it.key }, span = { item ->
                if (item is GridItem.Header) GridItemSpan(maxLineSpan) else GridItemSpan(1)
            }) { item ->
                ReorderableItem(reorderableState, key = item.key) { isDragging ->
                    when (item) {
                        is GridItem.Header -> {
                            val group = item.group
                            val headerDragModifier = if (editMode) Modifier.longPressDraggableHandle(
                                onDragStarted = { draggingGroupId = group.id },
                                onDragStopped = { draggingGroupId = null },
                            ) else Modifier
                            if (editMode) {
                                EditGroupHeader(
                                    group = group,
                                    canDelete = groups.size > 1,
                                    modifier = headerDragModifier,
                                    onRename = { renamingGroup = group },
                                    onDelete = { deletingGroup = group },
                                )
                            } else {
                                GroupLabel(group.name)
                            }
                        }
                        is GridItem.Sound -> {
                            if (draggingGroupId != null) {
                                Box(Modifier.height(0.dp))
                            } else {
                                val sound = BUILT_IN_SOUNDS.find { it.id == item.soundId }
                                    ?: return@ReorderableItem
                                val volume = state.volumes[sound.id] ?: 0f
                                val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)
                                val cardDragModifier = if (editMode) Modifier.longPressDraggableHandle(
                                    onDragStarted = { view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS) },
                                ) else Modifier
                                Box(
                                    Modifier
                                        .scale(scale)
                                        .then(if (editMode) Modifier.border(
                                            if (isDragging) 1.5.dp else 1.dp,
                                            if (isDragging) SonariTheme.colors.accentSolid else Color.White.copy(alpha = 0.12f),
                                            SonariTheme.shapes.md,
                                        ) else Modifier)
                                        .then(cardDragModifier),
                                ) {
                                    SoundCard(
                                        nameRes = sound.nameRes,
                                        iconRes = soundIconRes(sound.iconName),
                                        volume = volume,
                                        onVolumeChange = { v -> engine.setVolume(sound.id, v) },
                                        onToggle = { engine.toggle(sound.id) },
                                        enabled = !editMode,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (editMode) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilledTonalButton(
                        onClick = {
                            val id = "g_${System.currentTimeMillis()}"
                            val newGroup = SoundGroup(id, newGroupLabel, emptyList())
                            val updated = groups + newGroup
                            flat = groupsToFlat(updated)
                            renamingGroup = newGroup
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = spacing.sm).height(52.dp),
                        shape = SonariTheme.shapes.pill,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = colors.surfaceRaised, contentColor = colors.textBody),
                    ) {
                        Icon(painterResource(R.drawable.ic_plus), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(spacing.sm))
                        Text(
                            stringResource(R.string.edit_add_group),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                }
            }
        }

        Box(
            Modifier.fillMaxWidth().height(100.dp).align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(colors.surfaceApp.copy(alpha = 0f), colors.surfaceApp)))
        )

        if (editMode) {
            Row(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .padding(horizontal = spacing.screenEdge, vertical = spacing.md),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                FilledTonalButton(
                    onClick = { flat = snapshot; editMode = false },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = SonariTheme.shapes.pill,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = colors.surfacePressed, contentColor = colors.textStrong),
                ) {
                    Text(stringResource(R.string.edit_cancel), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                }
                Box(
                    Modifier.weight(1f).height(52.dp).clip(SonariTheme.shapes.pill)
                        .background(colors.accentGradient)
                        .clickable {
                            snapshot = flat
                            scope.launch { AppPrefs.setSoundGroups(context, groups) }
                            editMode = false
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(R.string.edit_done), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = colors.textOnAccent)
                }
            }
        } else {
            Row(
                Modifier.align(Alignment.BottomEnd)
                    .padding(end = spacing.screenEdge, bottom = spacing.md),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledIconButton(
                    onClick = { snapshot = flat; editMode = true },
                    modifier = Modifier.size(BottomBarButtonSize).shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.surfacePressed),
                ) {
                    Icon(painterResource(R.drawable.ic_pencil), stringResource(R.string.edit_layout), tint = colors.textStrong, modifier = Modifier.size(22.dp))
                }
                FilledIconButton(
                    onClick = { showTimerSheet = true },
                    modifier = Modifier.size(BottomBarButtonSize).shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.surfacePressed),
                ) {
                    Icon(
                        painter = painterResource(if (timerLabel != null) R.drawable.ic_timer else R.drawable.ic_moon),
                        contentDescription = stringResource(R.string.sleep_timer),
                        tint = if (timerLabel != null) colors.accentSolid else colors.textStrong,
                        modifier = Modifier.size(24.dp),
                    )
                }
                val playButtonCorner by animateDpAsState(
                    targetValue = if (state.isPlaying) 20.dp else BottomBarButtonSize / 2,
                    label = "playButtonCorner",
                )
                val playButtonShape = RoundedCornerShape(playButtonCorner)
                Box(
                    Modifier.size(BottomBarButtonSize).shadow(8.dp, playButtonShape)
                        .clip(playButtonShape).background(colors.accentGradient)
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            MixController.togglePlay(context)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = stringResource(if (state.isPlaying) R.string.pause else R.string.play),
                        tint = colors.textOnAccent,
                        modifier = Modifier.size(25.dp),
                    )
                }
            }
        }
    }

    renamingGroup?.let { group ->
        RenameGroupSheet(
            group = group,
            onConfirm = { newName ->
                val updated = groups.map { if (it.id == group.id) it.copy(name = newName) else it }
                flat = groupsToFlat(updated)
                renamingGroup = null
            },
            onDismiss = { renamingGroup = null },
        )
    }

    if (deletingGroup != null) {
        val group = deletingGroup!!
        val colors = SonariTheme.colors
        AlertDialog(
            onDismissRequest = { deletingGroup = null },
            title = { Text(stringResource(R.string.edit_delete_group_title)) },
            text = { Text(stringResource(R.string.edit_delete_group_body, group.name)) },
            confirmButton = {
                TextButton(onClick = {
                    flat = groupsToFlat(groups.withGroupDeleted(group.id))
                    deletingGroup = null
                }) {
                    Text(stringResource(R.string.edit_delete_group_confirm), color = colors.accentSolid)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingGroup = null }) {
                    Text(stringResource(R.string.edit_cancel))
                }
            },
        )
    }

    if (showTimerSheet) {
        SleepTimerSheet(
            currentMinutes = timerMinutes,
            onSelect = { minutes -> timer.set(minutes); showTimerSheet = false },
            onDismiss = { showTimerSheet = false },
        )
    }
}

@Composable
private fun GroupLabel(name: String) {
    val spacing = SonariTheme.spacing
    Text(
        text = name.uppercase(java.util.Locale.getDefault()),
        style = TextStyle(fontFamily = SonariSans, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.12.em),
        color = SonariTheme.colors.textFaint,
        modifier = Modifier.fillMaxWidth().padding(top = spacing.sm, bottom = spacing.xs),
    )
}

@Composable
private fun EditGroupHeader(
    group: SoundGroup,
    canDelete: Boolean,
    modifier: Modifier = Modifier,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    Row(
        Modifier.fillMaxWidth().padding(top = spacing.sm, bottom = spacing.xs).then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Icon(painterResource(R.drawable.ic_grip), contentDescription = null, tint = colors.textFaint, modifier = Modifier.size(16.dp))
        Text(
            text = group.name.uppercase(java.util.Locale.getDefault()),
            style = TextStyle(fontFamily = SonariSans, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.12.em),
            color = colors.textFaint,
            modifier = Modifier.weight(1f),
        )
        FilledIconButton(
            onClick = onRename,
            modifier = Modifier.size(28.dp),
            shape = SonariTheme.shapes.sm,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.surfaceRaised),
        ) {
            Icon(
                painterResource(R.drawable.ic_pencil),
                contentDescription = stringResource(R.string.edit_rename_action, group.name),
                tint = colors.textMuted,
                modifier = Modifier.size(14.dp),
            )
        }
        if (canDelete) {
            FilledIconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp),
                shape = SonariTheme.shapes.sm,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = colors.surfaceRaised),
            ) {
                Icon(painterResource(R.drawable.ic_trash_2), contentDescription = stringResource(R.string.edit_delete_group), tint = colors.textFaint, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenameGroupSheet(
    group: SoundGroup,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    var name by remember(group.id) { mutableStateOf(group.name) }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = colors.surfaceCard) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = spacing.screenEdge).padding(bottom = spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(stringResource(R.string.edit_rename_group), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = colors.textStrong)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.edit_group_name_hint), color = colors.textFaint) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentSolid,
                    unfocusedBorderColor = colors.textFaint,
                    focusedTextColor = colors.textStrong,
                    unfocusedTextColor = colors.textStrong,
                    cursorColor = colors.accentSolid,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (name.isNotBlank()) onConfirm(name.trim()) }),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                FilledTonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = SonariTheme.shapes.pill,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = colors.surfacePressed, contentColor = colors.textStrong),
                ) {
                    Text(stringResource(R.string.edit_cancel), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                }
                Box(
                    Modifier.weight(1f).height(52.dp).clip(SonariTheme.shapes.pill)
                        .then(if (name.isNotBlank()) Modifier.background(colors.accentGradient) else Modifier.background(colors.surfacePressed))
                        .clickable(enabled = name.isNotBlank()) { onConfirm(name.trim()) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(R.string.edit_rename),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (name.isNotBlank()) colors.textOnAccent else colors.textMuted,
                    )
                }
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
        Image(painterResource(R.drawable.ic_sonari_mark), contentDescription = null, modifier = Modifier.size(30.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = SonariTheme.colors.textStrong,
        )
    }
}

private val BottomBarButtonSize = 62.dp
