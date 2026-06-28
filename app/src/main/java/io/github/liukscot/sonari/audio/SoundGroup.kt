package io.github.liukscot.sonari.audio

import android.content.Context

data class SoundGroup(val id: String, val name: String, val soundIds: List<String>)

fun defaultSoundGroups(context: Context): List<SoundGroup> =
    BUILT_IN_SOUNDS.groupBy { it.categoryRes }
        .map { (res, sounds) ->
            val name = context.getString(res)
            SoundGroup(
                id = name.lowercase(java.util.Locale.getDefault()).replace(' ', '_'),
                name = name,
                soundIds = sounds.map { it.id },
            )
        }

fun List<SoundGroup>.withGroupDeleted(id: String): List<SoundGroup> {
    val idx = indexOfFirst { it.id == id }
    if (idx < 0) return this
    val targetIdx = if (idx < size - 1) idx + 1 else idx - 1
    return mapIndexed { i, g ->
        if (i == targetIdx) g.copy(soundIds = g.soundIds + this[idx].soundIds) else g
    }.filterNot { it.id == id }
}

internal fun List<SoundGroup>.serialize(): String =
    joinToString("|") { g -> "${g.id}~${g.name}~${g.soundIds.joinToString(",")}" }

internal fun String.deserializeGroups(): List<SoundGroup>? =
    takeIf { isNotBlank() }
        ?.split("|")
        ?.mapNotNull { part ->
            val p = part.split("~", limit = 3)
            if (p.size < 2) null
            else SoundGroup(p[0], p[1], if (p.size > 2 && p[2].isNotEmpty()) p[2].split(",") else emptyList())
        }
        ?.takeIf { it.isNotEmpty() }
