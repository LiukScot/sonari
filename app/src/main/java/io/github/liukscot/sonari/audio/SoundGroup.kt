package io.github.liukscot.sonari.audio

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

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
    JSONArray().also { arr ->
        forEach { g ->
            arr.put(JSONObject().apply {
                put("id", g.id)
                put("name", g.name)
                put("sounds", JSONArray(g.soundIds))
            })
        }
    }.toString()

internal fun String.deserializeGroups(): List<SoundGroup>? =
    runCatching {
        val arr = JSONArray(this)
        (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            val sounds = obj.getJSONArray("sounds")
            SoundGroup(
                id = obj.getString("id"),
                name = obj.getString("name"),
                soundIds = (0 until sounds.length()).map { j -> sounds.getString(j) },
            )
        }.takeIf { it.isNotEmpty() }
    }.getOrNull()
