package io.github.liukscot.sonari.audio

/* Linear master fade (PLAN: ~1s on global play/pause). factorAt maps elapsed
   time to a 0f..1f multiplier; the engine multiplies every active sound's
   volume by it so the whole mix swells in / out together. Pure on purpose —
   this is the unit-tested part, the ExoPlayer wiring is not. */
object Fade {
    const val DEFAULT_DURATION_MS = 1000L

    fun factorAt(elapsedMs: Long, durationMs: Long = DEFAULT_DURATION_MS): Float {
        if (durationMs <= 0L) return 1f
        return (elapsedMs.toFloat() / durationMs).coerceIn(0f, 1f)
    }
}
