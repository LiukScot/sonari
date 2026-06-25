package io.github.liukscot.sonari.audio

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/* Stops the mix after a chosen number of minutes, with the engine's normal ~1s
   fade-out. Lives process-wide (created in SonariPlayback) and runs on a
   coroutine delay, not an AlarmManager: while a mix plays the foreground
   service keeps the process alive, so the delay survives the screen going off —
   which is all this needs. On expiry it pauses the engine (the engine fades).

   Exposes both the chosen [minutes] (to highlight the active preset) and the
   absolute [endRealtimeMs] (for the live countdown in the playback bar). */
class SleepTimer(
    private val scope: CoroutineScope,
    private val onExpire: () -> Unit,
    private val now: () -> Long = { SystemClock.elapsedRealtime() },
) {
    private val _minutes = MutableStateFlow(0)
    val minutes: StateFlow<Int> = _minutes.asStateFlow()

    private val _endRealtimeMs = MutableStateFlow<Long?>(null)
    val endRealtimeMs: StateFlow<Long?> = _endRealtimeMs.asStateFlow()

    private var job: Job? = null

    /** Set the timer to [minutes] from now; 0 turns it off. */
    fun set(minutes: Int) {
        cancel()
        if (minutes <= 0) return
        _minutes.value = minutes
        _endRealtimeMs.value = now() + minutes * 60_000L
        job = scope.launch {
            delay(minutes * 60_000L)
            reset()
            onExpire()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
        reset()
    }

    private fun reset() {
        _minutes.value = 0
        _endRealtimeMs.value = null
    }
}
