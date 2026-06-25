package io.github.liukscot.sonari.ui.presets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.ui.theme.SonariTheme

@Composable
fun PresetsScreen(modifier: Modifier = Modifier) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    Box(
        modifier.fillMaxSize().background(colors.surfaceApp).padding(spacing.screenEdge),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(
            text = stringResource(R.string.nav_presets),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textStrong,
        )
    }
}
