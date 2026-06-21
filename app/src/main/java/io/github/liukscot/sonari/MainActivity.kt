package io.github.liukscot.sonari

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.liukscot.sonari.ui.theme.SonariTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SonariTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SoundboardScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SoundboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // SoundPool: low-latency mixer for short SFX. Built once, reused.
    val soundPool = remember {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(4)        // up to 4 overlapping plays
            .setAudioAttributes(attrs)
            .build()
    }

    // load() is async — returns soundId, but sample not playable until loaded
    val soundId = remember { soundPool.load(context, R.raw.among_us_role_reveal, 1) }

    // release native pool when composable leaves
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Soundboard")

        Button(
            onClick = {
                // play(soundID, leftVol, rightVol, priority, loop, rate)
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        ) {
            Text("Play Among Us")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SoundboardPreview() {
    SonariTheme {
        SoundboardScreen()
    }
}
