package io.github.liukscot.sonari

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.audio.SonariPlayback
import io.github.liukscot.sonari.ui.SonariApp
import io.github.liukscot.sonari.ui.theme.SonariTheme

class MainActivity : ComponentActivity() {
    private lateinit var engine: AudioEngine

    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* notification is optional */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Process-wide engine, shared with the Service and the Quick Tile — not
        // released here (the service owns its lifecycle).
        engine = SonariPlayback.get(applicationContext)
        requestNotificationPermission()
        // Force dark (light) system-bar icons regardless of the system day/night
        // setting — the app is dark-only.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            SonariTheme {
                SonariApp(engine, modifier = Modifier.fillMaxSize())
            }
        }
    }

    /* The foreground media notification needs the runtime permission on API 33+.
       Playback still works if denied; only the notification is suppressed. */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
