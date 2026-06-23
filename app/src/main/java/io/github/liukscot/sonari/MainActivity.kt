package io.github.liukscot.sonari

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.ui.mixer.MixerScreen
import io.github.liukscot.sonari.ui.theme.SonariTheme

class MainActivity : ComponentActivity() {
    private lateinit var engine: AudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        engine = AudioEngine(applicationContext)
        // Force dark (light) system-bar icons regardless of the system day/night
        // setting — the app is dark-only.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            SonariTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MixerScreen(engine, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        engine.release()
        super.onDestroy()
    }
}
