package io.github.liukscot.sonari.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val navShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(navShape)
            .background(colors.surfaceCard)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Row(
            Modifier.fillMaxWidth().height(NavBarHeight),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NAV_ITEMS.forEach { item ->
                NavBarItem(
                    item = item,
                    selected = current == item.tab,
                    onClick = { onSelect(item.tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = SonariTheme.colors
    val tint = if (selected) colors.accentSolid else colors.textFaint
    val interactionSource = remember { MutableInteractionSource() }
    val view = LocalView.current
    Column(
        modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false),
                onClick = {
                    if (!selected) view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    onClick()
                },
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(item.labelRes),
            fontFamily = SonariSans,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
        )
    }
}

private val NavBarHeight = 62.dp
