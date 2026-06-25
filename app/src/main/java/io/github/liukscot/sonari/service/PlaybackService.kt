package io.github.liukscot.sonari.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import io.github.liukscot.sonari.MainActivity
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.MixController
import io.github.liukscot.sonari.audio.MixFacadePlayer
import io.github.liukscot.sonari.audio.PlaybackState
import io.github.liukscot.sonari.audio.SonariPlayback
import io.github.liukscot.sonari.ui.theme.SonariDarkColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/* Foreground MediaSessionService that keeps the mix playing with the screen
   off and shows the media notification.

   It holds no audio state: the engine is the process-wide one (SonariPlayback),
   wrapped by a MixFacadePlayer so the session has the single Player it expects.
   The foreground notification is managed here by hand rather than left to
   Media3's automatic provider: playback starts inside the engine (a sound
   auto-plays on tap), not through a MediaController command, and Media3's
   auto-notification only fires for session-routed commands — so without this
   the service is started but never calls startForeground() and the system
   kills it (ForegroundServiceDidNotStartInTimeException). The MediaStyle still
   links the notification to the session for the system media controls. */
class PlaybackService : MediaSessionService() {

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var session: MediaSession? = null
    private var stateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        val engine = SonariPlayback.get(this)
        val player = MixFacadePlayer(engine, scope, getString(R.string.playback_notification_title))
        session = MediaSession.Builder(this, player).build()
        player.startObserving()
        stateJob = scope.launch {
            engine.state.collect { applyForeground(it) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_TOGGLE) MixController.togglePlay(this)
        // Promote to foreground immediately so we never miss the startForeground
        // deadline, regardless of when the state collector first runs.
        applyForeground(SonariPlayback.get(this).state.value)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = session

    private fun applyForeground(state: PlaybackState) {
        val s = session ?: return
        if (state.volumes.isEmpty()) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sonari_mark)
            .setLargeIcon(MixArtwork.get())
            .setColorized(true)
            .setColor(SonariDarkColors.accentSolid.toArgb())
            .setContentTitle(getString(R.string.playback_notification_title))
            .setContentIntent(contentIntent())
            .setOngoing(state.isPlaying)
            .addAction(
                if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                getString(if (state.isPlaying) R.string.pause else R.string.play),
                togglePendingIntent(),
            )
            .setStyle(MediaStyleNotificationHelper.MediaStyle(s).setShowActionsInCompactView(0))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        // Always startForeground first: if the service was started with
        // startForegroundService() it must call this within the deadline, even
        // if the state flipped to paused in between (else the system kills it).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        if (!state.isPlaying) {
            // Paused: drop the foreground lock but keep the notification
            // dismissible with a play button, so the mix can be resumed.
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        }
    }

    /* App swiped away from recents: stop unless still playing. The facade player
       is released here, not the engine — the engine is process-wide. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        stateJob?.cancel()
        session?.run {
            player.release()
            release()
        }
        session = null
        scope.cancel()
        super.onDestroy()
    }

    private fun contentIntent() = PendingIntent.getActivity(
        this, 0, Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE,
    )

    private fun togglePendingIntent() = PendingIntent.getService(
        this, 0, Intent(this, PlaybackService::class.java).setAction(ACTION_TOGGLE),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.playback_notification_title),
            NotificationManager.IMPORTANCE_LOW,
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "playback"
        const val NOTIFICATION_ID = 1
        const val ACTION_TOGGLE = "io.github.liukscot.sonari.TOGGLE"
    }
}
