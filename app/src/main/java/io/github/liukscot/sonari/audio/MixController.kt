package io.github.liukscot.sonari.audio

import android.content.Context
import androidx.annotation.MainThread

/* The play/pause entry point shared by the UI and the Quick Tile. The
   foreground service is started reactively in SonariPlayback when playback
   begins, so callers only restore the mix (cold start) and toggle. */
object MixController {

    /* Restore the persisted mix into the engine when it is empty (cold start
       from the Tile). Returns false when there is nothing to play. Suspends on
       the DataStore read; the engine touch is main-thread. */
    @MainThread
    suspend fun ensureLoaded(context: Context): Boolean {
        val engine = SonariPlayback.get(context)
        if (engine.state.value.volumes.isNotEmpty()) return true
        val mix = MixStore.load(context)
        if (mix.volumes.isEmpty()) return false
        engine.loadMix(mix.volumes, mix.master)
        return true
    }

    /** Toggle the whole mix. The foreground service follows the isPlaying edge. */
    @MainThread
    fun togglePlay(context: Context) {
        SonariPlayback.get(context).togglePlay()
    }
}
