package io.github.liukscot.sonari.audio

import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/* A one-track Player facade over the multi-loop AudioEngine. MediaSession wants
   a single Player, but the engine runs one ExoPlayer per sound — so this
   exposes only the master play/pause of the whole mix and forwards commands to
   the engine. It owns no audio; the engine does. invalidateState() is fired on
   every engine state change so the media notification and the Quick Tile stay
   in sync. */
class MixFacadePlayer(
    private val engine: AudioEngine,
    private val scope: CoroutineScope,
    title: String,
) : SimpleBasePlayer(Looper.getMainLooper()) {

    private val mediaItemData = MediaItemData.Builder("sonari_mix")
        .setMediaItem(MediaItem.Builder().setMediaId("sonari_mix").build())
        .setMediaMetadata(MediaMetadata.Builder().setTitle(title).build())
        .build()

    /* Stays false until the session is registered. The engine is usually
       already playing by the time the service is built (a sound auto-plays on
       tap, before the service starts). Reporting idle until startObserving()
       means the first observed state flips idle -> playing while the session is
       live, so MediaSessionService sees the transition and posts the foreground
       notification. Begin observing in init and the transition is consumed
       before the session listens, and startForeground() never fires. */
    private var observing = false

    fun startObserving() {
        if (observing) return
        observing = true
        scope.launch { engine.state.collect { invalidateState() } }
    }

    override fun getState(): State {
        val s = engine.state.value
        val active = observing && s.volumes.isNotEmpty()
        return State.Builder()
            .setAvailableCommands(COMMANDS)
            .setPlaybackState(if (active) Player.STATE_READY else Player.STATE_IDLE)
            .setPlayWhenReady(observing && s.isPlaying, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            .setPlaylist(listOf(mediaItemData))
            .setCurrentMediaItemIndex(0)
            .build()
    }

    override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {
        if (playWhenReady) engine.play() else engine.pause()
        return Futures.immediateVoidFuture()
    }

    override fun handleStop(): ListenableFuture<*> {
        engine.pause()
        return Futures.immediateVoidFuture()
    }

    private companion object {
        val COMMANDS = Player.Commands.Builder()
            .addAll(
                Player.COMMAND_PLAY_PAUSE,
                Player.COMMAND_STOP,
                Player.COMMAND_GET_CURRENT_MEDIA_ITEM,
                Player.COMMAND_GET_METADATA,
                Player.COMMAND_GET_TIMELINE,
            )
            .build()
    }
}
