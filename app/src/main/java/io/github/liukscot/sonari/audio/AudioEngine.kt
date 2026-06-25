package io.github.liukscot.sonari.audio

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.MainThread
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlin.math.abs
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
    val masterVolume: Float = 1f,                  // scales the whole mix, 0f..1f
)

/* Multi-loop player. One ExoPlayer per sound, created lazily on first use,
   looping seamlessly (REPEAT_MODE_ONE). The whole mix fades in/out together on
   togglePlay (master-only fade). Audio focus is owned once by the engine, not
   per-player: with one ExoPlayer per sound, per-player focus would make each
   new sound steal focus and pause the others. Public methods are expected on
   the main thread (the Compose UI calls them there); the Phase 2 Service/Tile
   path will need a main-dispatcher hop. */
class AudioEngine(private val context: Context) {

    private val players = mutableMapOf<String, ExoPlayer>()
    private val lastVolumes = mutableMapOf<String, Float>()  // last non-zero level per sound
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var fadeJob: Job? = null
    private var masterFactor = 0f  // current fade multiplier, 0f..1f

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var resumeOnFocusGain = false
    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN ->
                if (resumeOnFocusGain) { resumeOnFocusGain = false; play() }
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                stopImmediately()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                if (_state.value.isPlaying) { resumeOnFocusGain = true; pauseInternal(abandon = false) }
        }
    }

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    /** Set a sound's volume; 0 drops it from the mix. Player is created lazily. */
    @MainThread
    fun setVolume(soundId: String, volume: Float) {
        requireMainThread()
        val v = volume.coerceIn(0f, 1f)
        val wasActive = _state.value.volumes.containsKey(soundId)
        val wasPlaying = _state.value.isPlaying
        val volumes = _state.value.volumes.toMutableMap()
        if (v <= 0f) {
            volumes.remove(soundId)
            players[soundId]?.pause()
        } else {
            volumes[soundId] = v
            lastVolumes[soundId] = v
            val player = players.getOrPut(soundId) { createPlayer(soundId) }
            if (_state.value.isPlaying && !player.isPlaying) player.play()
        }
        _state.value = _state.value.copy(volumes = volumes)
        applyVolumes()
        when {
            // Removing the last sound stops the mix (don't keep focus/playing at 0).
            volumes.isEmpty() && wasPlaying -> { resumeOnFocusGain = false; stopImmediately() }
            // Activating a sound (off -> on) auto-starts; adjusting an already-active
            // one while paused leaves the pause alone.
            v > 0f && !wasActive && !_state.value.isPlaying -> play()
        }
    }

    /* Load a persisted mix into the engine without starting playback. Used on
       cold start (Quick Tile) to restore the last mix before the caller decides
       to play. No-op while already playing, so it never clobbers a live mix. */
    @MainThread
    fun loadMix(volumes: Map<String, Float>, master: Float) {
        requireMainThread()
        if (_state.value.isPlaying) return
        // Persisted ids may be stale (a sound dropped in a later version) — keep
        // only ids still in the catalog, else play() would crash creating them.
        val known = BUILT_IN_SOUNDS.mapTo(HashSet()) { it.id }
        val clean = volumes
            .filterKeys { it in known }
            .mapValues { it.value.coerceIn(0f, 1f) }
            .filterValues { it > 0f }
        clean.forEach { (id, v) -> lastVolumes[id] = v }
        _state.value = _state.value.copy(volumes = clean, masterVolume = master.coerceIn(0f, 1f))
        applyVolumes()
    }

    /** Toggle a sound: off if active, else restore its last level (or the default). */
    @MainThread
    fun toggle(soundId: String) {
        requireMainThread()
        val current = _state.value.volumes[soundId] ?: 0f
        setVolume(soundId, if (current > 0f) 0f else (lastVolumes[soundId] ?: DEFAULT_VOLUME))
    }

    /** Master level applied on top of each sound's own volume (and the fade). */
    @MainThread
    fun setMasterVolume(volume: Float) {
        requireMainThread()
        _state.value = _state.value.copy(masterVolume = volume.coerceIn(0f, 1f))
        applyVolumes()
    }

    @MainThread
    fun togglePlay() {
        requireMainThread()
        if (_state.value.isPlaying) pause() else play()
    }

    /** Start every active sound and fade the mix in. No-op if nothing is active
        or audio focus is denied. */
    @MainThread
    fun play() {
        requireMainThread()
        if (_state.value.isPlaying || _state.value.volumes.isEmpty()) return
        if (!requestFocus()) return
        _state.value = _state.value.copy(isPlaying = true)
        _state.value.volumes.keys.forEach { id ->
            players.getOrPut(id) { createPlayer(id) }.play()
        }
        fadeTo(target = 1f)
    }

    /** Fade the mix out, then pause every player (kept for reuse). */
    @MainThread
    fun pause() = pauseInternal(abandon = true)

    private fun pauseInternal(abandon: Boolean) {
        requireMainThread()
        if (!_state.value.isPlaying) return
        _state.value = _state.value.copy(isPlaying = false)
        // Abandon focus only after the fade finishes, so we keep focus while
        // still audible (a graceful, user-initiated pause).
        fadeTo(target = 0f, thenPause = true, thenAbandon = abandon)
    }

    /* Stop now, no fade — for permanent focus loss (another app took over) and
       when the last sound is removed: staying audible without focus is wrong. */
    private fun stopImmediately() {
        requireMainThread()
        fadeJob?.cancel()
        _state.value = _state.value.copy(isPlaying = false)
        masterFactor = 0f
        applyVolumes()
        players.values.forEach { it.pause() }
        abandonFocus()
    }

    @MainThread
    fun release() {
        requireMainThread()
        abandonFocus()
        resumeOnFocusGain = false
        fadeJob?.cancel()
        players.values.forEach { it.release() }
        players.clear()
        masterFactor = 0f
        _state.value = PlaybackState()
    }

    /* Single audio-focus owner for the whole mix (see class header). */
    private fun requestFocus(): Boolean {
        val attrs = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attrs)
                .setOnAudioFocusChangeListener(focusListener)
                .build()
            focusRequest = req
            audioManager.requestAudioFocus(req) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN,
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            focusRequest = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    /* Ramp masterFactor from its current value to [target]. The duration scales
       with the distance, so cancelling an in-flight fade and reversing keeps a
       constant ramp speed (a half-done fade reverses in ~half the time) instead
       of always taking the full duration. */
    private fun fadeTo(target: Float, thenPause: Boolean = false, thenAbandon: Boolean = false) {
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
            if (thenAbandon) abandonFocus()
        }
    }

    private fun applyVolumes() {
        val s = _state.value
        players.forEach { (id, p) -> p.volume = (s.volumes[id] ?: 0f) * s.masterVolume * masterFactor }
    }

    private fun requireMainThread() = check(Looper.myLooper() == Looper.getMainLooper()) {
        "AudioEngine methods must be called on the main thread"
    }

    private fun createPlayer(soundId: String): ExoPlayer {
        val sound = BUILT_IN_SOUNDS.firstOrNull { it.id == soundId }
            ?: error("Unknown sound id: $soundId")
        val uri = "android.resource://${context.packageName}/${sound.resId}"
        val attrs = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        return ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(attrs, /* handleAudioFocus = */ false)
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(uri))
            volume = 0f
            prepare()
        }
    }

    private companion object {
        const val STEP_MS = 32L          // ~30 fps volume ramp
        const val DEFAULT_VOLUME = 0.5f  // first-time level when no previous one exists
    }
}
