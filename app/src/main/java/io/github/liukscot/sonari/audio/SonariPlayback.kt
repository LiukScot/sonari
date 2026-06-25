package io.github.liukscot.sonari.audio

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import io.github.liukscot.sonari.service.PlaybackService
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/* Process-wide playback handle. The UI and the Service / Quick Tile all read
   the same engine from here — the engine owns the state, callers keep none of
   their own. This is the hook the Tile uses when no Activity exists.

   Creating the engine wires two process-wide collectors:
   - persistence: saves the mix (debounced) so the Tile can replay it cold.
   - foreground service: starts PlaybackService whenever playback begins, from
     any trigger (a sound auto-plays on activation, the play button, the tile).
     Tying it to the isPlaying edge — not to one entry point — is why activating
     a sound from the grid still keeps audio alive with the screen off. */
object SonariPlayback {
    private const val TAG = "SonariPlayback"

    @Volatile private var engine: AudioEngine? = null
    @Volatile private var timer: SleepTimer? = null
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    @OptIn(kotlinx.coroutines.FlowPreview::class)  // debounce
    fun get(context: Context): AudioEngine =
        engine ?: synchronized(this) {
            engine ?: AudioEngine(context.applicationContext).also { created ->
                timer = SleepTimer(scope = scope, onExpire = { created.pause() })
                engine = created
                val app = context.applicationContext
                scope.launch {
                    created.state
                        .map { it.volumes to it.masterVolume }
                        .distinctUntilChanged()
                        .debounce(SAVE_DEBOUNCE_MS)
                        .collect { (volumes, master) ->
                            // A failed save must not cancel the collector, or no
                            // later change would ever be persisted.
                            try {
                                MixStore.save(app, volumes, master)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: IOException) {
                                Log.w(TAG, "Could not persist mix", e)
                            }
                        }
                }
                scope.launch {
                    created.state
                        .map { it.isPlaying }
                        .distinctUntilChanged()
                        .collect { playing -> if (playing) startService(app) }
                }
            }
        }

    /** The process-wide sleep timer (shares the engine's lifetime). */
    fun sleepTimer(context: Context): SleepTimer {
        get(context)
        return timer!!
    }

    private fun startService(app: Context) {
        try {
            ContextCompat.startForegroundService(app, Intent(app, PlaybackService::class.java))
        } catch (e: IllegalStateException) {
            // Android 12+ forbids starting a foreground service from the
            // background. Playback (e.g. a focus-regain resume) continues
            // without the service; only screen-off survival is at risk here.
            Log.w(TAG, "Could not start playback service from background", e)
        }
    }

    private const val SAVE_DEBOUNCE_MS = 500L
}
