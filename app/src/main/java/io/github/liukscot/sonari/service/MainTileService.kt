package io.github.liukscot.sonari.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.MixController
import io.github.liukscot.sonari.audio.SonariPlayback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/* Quick Settings tile that toggles the last mix straight from the shade, even
   when the app process is dead — onClick restores the persisted mix before
   playing. While the panel is open the tile mirrors the real playing state, so
   it never shows a stale toggle. */
class MainTileService : TileService() {

    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var stateJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        val engine = SonariPlayback.get(this)
        stateJob = scope.launch {
            engine.state.collect { syncTile(it.isPlaying) }
        }
    }

    override fun onStopListening() {
        stateJob?.cancel()
        stateJob = null
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()
        scope.launch {
            if (MixController.ensureLoaded(this@MainTileService)) {
                MixController.togglePlay(this@MainTileService)
            }
        }
    }

    private fun syncTile(playing: Boolean) {
        val tile = qsTile ?: return
        tile.state = if (playing) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.tile_label)
        tile.updateTile()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
