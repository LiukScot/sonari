package io.github.liukscot.sonari.audio

import android.content.Context
import android.os.SystemClock
import kotlin.math.abs
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Active mix: which sounds play and at what volume, plus the global flag. */
data class PlaybackState(
    val isPlaying: Boolean = false,
    val volumes: Map<String, Float> = emptyMap(),  // soundId -> 0f..1f, active only
)

/* Multi-loop player. One ExoPlayer per sound, created lazily on first use,
   looping seamlessly (REPEAT_MODE_ONE) with audio focus. The whole mix fades
   in/out together on togglePlay (master-only fade). Public methods are
   expected on the main thread (the Compose UI calls them there); the Phase 2
   Service/Tile path will need a main-dispatcher hop. */
class AudioEngine(private val context: Context) {

    private val players = mutableMapOf<String, ExoPlayer>()
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var fadeJob: Job? = null
    private var masterFactor = 0f  // current fade multiplier, 0f..1f

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    /** Set a sound's volume; 0 drops it from the mix. Player is created lazily. */
    fun setVolume(soundId: String, volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        val volumes = _state.value.volumes.toMutableMap()
        if (v <= 0f) {
            volumes.remove(soundId)
            players[soundId]?.volume = 0f
        } else {
            volumes[soundId] = v
            val player = players.getOrPut(soundId) { createPlayer(soundId) }
            if (_state.value.isPlaying) {
                player.volume = v * masterFactor
                if (!player.isPlaying) player.play()
            }
        }
        _state.value = _state.value.copy(volumes = volumes)
    }

    fun togglePlay() {
        if (_state.value.isPlaying) pause() else play()
    }

    /** Start every active sound and fade the mix in. No-op if nothing is active. */
    fun play() {
        if (_state.value.isPlaying || _state.value.volumes.isEmpty()) return
        _state.value = _state.value.copy(isPlaying = true)
        _state.value.volumes.keys.forEach { id ->
            players.getOrPut(id) { createPlayer(id) }.play()
        }
        fadeTo(target = 1f)
    }

    /** Fade the mix out, then pause every player (kept for reuse). */
    fun pause() {
        if (!_state.value.isPlaying) return
        _state.value = _state.value.copy(isPlaying = false)
        fadeTo(target = 0f, thenPause = true)
    }

    fun release() {
        fadeJob?.cancel()
        players.values.forEach { it.release() }
        players.clear()
        masterFactor = 0f
        _state.value = PlaybackState()
    }

    /* Ramp masterFactor from its current value to [target]. The duration scales
       with the distance, so cancelling an in-flight fade and reversing keeps a
       constant ramp speed (a half-done fade reverses in ~half the time) instead
       of always taking the full duration. */
    private fun fadeTo(target: Float, thenPause: Boolean = false) {
        fadeJob?.cancel()
        val from = masterFactor
        fadeJob = scope.launch {
            val duration = (Fade.DEFAULT_DURATION_MS * abs(target - from)).toLong()
            val start = SystemClock.elapsedRealtime()
            while (isActive) {
                val t = Fade.factorAt(SystemClock.elapsedRealtime() - start, duration)
                masterFactor = from + (target - from) * t
                applyVolumes()
                if (t >= 1f) break
                delay(STEP_MS)
            }
            masterFactor = target
            applyVolumes()
            if (thenPause) players.values.forEach { it.pause() }
        }
    }

    private fun applyVolumes() {
        val volumes = _state.value.volumes
        players.forEach { (id, p) -> p.volume = (volumes[id] ?: 0f) * masterFactor }
    }

    private fun createPlayer(soundId: String): ExoPlayer {
        val sound = BUILT_IN_SOUNDS.first { it.id == soundId }
        val uri = "android.resource://${context.packageName}/${sound.resId}"
        val attrs = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        return ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(attrs, /* handleAudioFocus = */ true)
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(uri))
            volume = 0f
            prepare()
        }
    }

    private companion object {
        const val STEP_MS = 32L  // ~30 fps volume ramp
    }
}
