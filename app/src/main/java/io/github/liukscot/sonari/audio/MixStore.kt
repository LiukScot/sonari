package io.github.liukscot.sonari.audio

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.mixDataStore by preferencesDataStore(name = "mix")

/* The last mix, persisted so the Quick Tile can replay it from a cold process
   (the app may be dead when the tile fires). Volumes are flattened to one
   string "id=v;id=v" — a handful of sounds, not worth a typed schema. The
   play flag is intentionally not stored: the tile press itself means "play",
   and auto-resuming audio on a plain app open would be unwelcome. */
object MixStore {
    private const val TAG = "MixStore"
    private val VOLUMES = stringPreferencesKey("volumes")
    private val MASTER = floatPreferencesKey("master")

    suspend fun save(context: Context, volumes: Map<String, Float>, master: Float) {
        context.mixDataStore.edit { prefs ->
            prefs[VOLUMES] = encodeVolumes(volumes)
            prefs[MASTER] = master
        }
    }

    /* Returns the stored mix, or an empty default if nothing was saved or the
       store can't be read (corruption/IO) — the Tile must not crash on a cold
       start because of a bad read. */
    suspend fun load(context: Context): Mix =
        try {
            val prefs = context.mixDataStore.data.first()
            Mix(parseVolumes(prefs[VOLUMES]), prefs[MASTER] ?: 1f)
        } catch (e: Exception) {
            Log.w(TAG, "Could not read saved mix", e)
            Mix(emptyMap(), 1f)
        }

    internal fun encodeVolumes(volumes: Map<String, Float>): String =
        volumes.entries.joinToString(";") { "${it.key}=${it.value}" }

    /* Parse "id=v;id=v"; malformed entries (no '=', empty id, non-numeric value)
       are dropped rather than corrupting the whole mix. */
    internal fun parseVolumes(raw: String?): Map<String, Float> =
        raw.orEmpty()
            .split(';')
            .mapNotNull { entry ->
                val parts = entry.split('=')
                val value = parts.getOrNull(1)?.toFloatOrNull()
                if (parts.size == 2 && parts[0].isNotEmpty() && value != null) parts[0] to value else null
            }
            .toMap()

    data class Mix(val volumes: Map<String, Float>, val master: Float)
}
