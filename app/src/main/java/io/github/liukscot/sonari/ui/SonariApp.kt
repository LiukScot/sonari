package io.github.liukscot.sonari.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.AudioEngine
import io.github.liukscot.sonari.ui.mixer.MixerScreen
import io.github.liukscot.sonari.ui.presets.PresetsScreen
import io.github.liukscot.sonari.ui.settings.SettingsScreen
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme

private enum class Tab {
    Mixer, Presets, Settings
}

private data class NavItem(
    val tab: Tab,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
)

private val NAV_ITEMS = listOf(
    NavItem(Tab.Mixer, R.string.nav_mixer, R.drawable.ic_sliders_horizontal),
    NavItem(Tab.Presets, R.string.nav_presets, R.drawable.ic_layers),
    NavItem(Tab.Settings, R.string.nav_settings, R.drawable.ic_settings),
)

@Composable
fun SonariApp(engine: AudioEngine, modifier: Modifier = Modifier) {
    var tab by rememberSaveable { mutableStateOf(Tab.Mixer) }
    val colors = SonariTheme.colors

    Column(modifier.fillMaxSize().statusBarsPadding().background(colors.surfaceApp)) {
        Box(Modifier.weight(1f)) {
            when (tab) {
                Tab.Mixer -> MixerScreen(engine, Modifier.fillMaxSize())
                Tab.Presets -> PresetsScreen(engine, Modifier.fillMaxSize())
                Tab.Settings -> SettingsScreen(Modifier.fillMaxSize())
            }
        }
        Spacer(Modifier.height(2.dp))
        SonariNavBar(
            current = tab,
            onSelect = { tab = it },
        )
    }
}

@Composable
private fun SonariNavBar(current: Tab, onSelect: (Tab) -> Unit) {
    val colors = SonariTheme.colors
    val view = LocalView.current
    val navShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(navShape)
            .background(colors.surfaceCard)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        NavigationBar(
            modifier = Modifier.height(NavBarHeight),
            containerColor = colors.surfaceCard,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0.dp),
        ) {
            NAV_ITEMS.forEach { item ->
                val selected = current == item.tab
                NavigationBarItem(
                    selected = selected,
                    // ponytail: M3 centers this on its own 80dp token, not our 62dp bar,
                    // leaving more room below than above — nudged down by eye.
                    modifier = Modifier.offset(y = 5.dp),
                    onClick = {
                        if (!selected) view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        onSelect(item.tab)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.labelRes),
                            fontFamily = SonariSans,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.accentSolid,
                        selectedTextColor = colors.accentSolid,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = colors.textFaint,
                        unselectedTextColor = colors.textFaint,
                    ),
                )
            }
        }
    }
}

// M3 NavigationBar defaults to an 80dp token height; force it back to the
// compact twilight bar height (Compose honors a smaller incoming max height).
private val NavBarHeight = 62.dp
