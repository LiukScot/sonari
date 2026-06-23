package io.github.liukscot.sonari.audio

import android.content.Context
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
    private val VOLUMES = stringPreferencesKey("volumes")
    private val MASTER = floatPreferencesKey("master")

    suspend fun save(context: Context, volumes: Map<String, Float>, master: Float) {
        context.mixDataStore.edit { prefs ->
            prefs[VOLUMES] = volumes.entries.joinToString(";") { "${it.key}=${it.value}" }
            prefs[MASTER] = master
        }
    }

    /** Returns the stored mix, or an empty map if nothing was ever saved. */
    suspend fun load(context: Context): Mix {
        val prefs = context.mixDataStore.data.first()
        val volumes = prefs[VOLUMES].orEmpty()
            .split(';')
            .mapNotNull { entry ->
                val parts = entry.split('=')
                val v = parts.getOrNull(1)?.toFloatOrNull()
                if (parts.size == 2 && v != null) parts[0] to v else null
            }
            .toMap()
        return Mix(volumes, prefs[MASTER] ?: 1f)
    }

    data class Mix(val volumes: Map<String, Float>, val master: Float)
}
