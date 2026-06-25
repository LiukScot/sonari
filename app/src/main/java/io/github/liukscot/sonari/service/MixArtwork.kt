package io.github.liukscot.sonari.service

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.graphics.toArgb
import io.github.liukscot.sonari.ui.theme.Pink
import io.github.liukscot.sonari.ui.theme.Violet

/* The media-notification artwork: just the signature twilight gradient, the
   same violet->pink diagonal the active cards use. Android 13+ uses the large
   icon as the player background, so a clean smooth gradient reads well and
   never pixelates (a logo with thin strokes did, once the system scaled it).
   The app logo lives on the small icon instead. Cached — the notification
   rebuilds on every play/pause and must not redraw each time. */
object MixArtwork {
    private const val SIZE = 1024

    @Volatile private var cached: Bitmap? = null

    fun get(): Bitmap =
        cached ?: synchronized(this) { cached ?: render().also { cached = it } }

    private fun render(): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, SIZE.toFloat(), SIZE.toFloat(),
                Violet.toArgb(), Pink.toArgb(), Shader.TileMode.CLAMP,
            )
        }
        Canvas(bitmap).drawRect(0f, 0f, SIZE.toFloat(), SIZE.toFloat(), paint)
        return bitmap
    }
}
