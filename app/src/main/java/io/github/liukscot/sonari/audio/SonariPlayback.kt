package io.github.liukscot.sonari.audio

import android.content.Context

/* Process-wide playback handle. The UI and (Phase 2) the Service / Quick Tile
   all read the same engine from here — the engine owns the state, callers keep
   none of their own. This is the hook the Tile uses when no Activity exists. */
object SonariPlayback {
    @Volatile private var engine: AudioEngine? = null

    fun get(context: Context): AudioEngine =
        engine ?: synchronized(this) {
            engine ?: AudioEngine(context.applicationContext).also { engine = it }
        }
}
