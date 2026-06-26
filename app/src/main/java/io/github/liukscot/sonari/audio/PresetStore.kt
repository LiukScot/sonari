package io.github.liukscot.sonari.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private val Context.presetDataStore by preferencesDataStore(name = "presets")
private val PRESETS_KEY = stringPreferencesKey("presets")

object PresetStore {

    fun presets(context: Context): Flow<List<Preset>> =
        context.presetDataStore.data
            .catch { e ->
                if (e is CancellationException) throw e
                if (e !is IOException) throw e
                emit(emptyPreferences())
            }
            .map { prefs -> decode(prefs[PRESETS_KEY]) }

    suspend fun save(context: Context, preset: Preset) {
        context.presetDataStore.edit { prefs ->
            val list = decode(prefs[PRESETS_KEY]).toMutableList()
            val idx = list.indexOfFirst { it.id == preset.id }
            if (idx >= 0) list[idx] = preset else list.add(preset)
            prefs[PRESETS_KEY] = encode(list)
        }
    }

    suspend fun delete(context: Context, id: String) {
        context.presetDataStore.edit { prefs ->
            prefs[PRESETS_KEY] = encode(decode(prefs[PRESETS_KEY]).filter { it.id != id })
        }
    }

    suspend fun setTileEnabled(context: Context, id: String, enabled: Boolean) {
        context.presetDataStore.edit { prefs ->
            prefs[PRESETS_KEY] = encode(decode(prefs[PRESETS_KEY]).map { p ->
                if (p.id == id) p.copy(tileEnabled = enabled) else p
            })
        }
    }

    private fun encode(presets: List<Preset>): String = JSONArray().also { arr ->
        presets.forEach { p ->
            arr.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("iconName", p.iconName)
                put("volumes", JSONObject(p.volumes as Map<*, *>))
                put("masterVolume", p.masterVolume.toDouble())
                put("tileEnabled", p.tileEnabled)
            })
        }
    }.toString()

    private fun decode(json: String?): List<Preset> {
        if (json.isNullOrEmpty()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                val volObj = obj.getJSONObject("volumes")
                Preset(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    iconName = obj.getString("iconName"),
                    volumes = volObj.keys().asSequence().associateWith { k -> volObj.getDouble(k).toFloat() },
                    masterVolume = obj.getDouble("masterVolume").toFloat(),
                    tileEnabled = obj.optBoolean("tileEnabled", false),
                )
            }
        }.getOrElse { emptyList() }
    }
}
