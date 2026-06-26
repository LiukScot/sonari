package io.github.liukscot.sonari.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.liukscot.sonari.R
import io.github.liukscot.sonari.audio.AppPrefs
import io.github.liukscot.sonari.ui.theme.SonariSans
import io.github.liukscot.sonari.ui.theme.SonariTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    val shapes = SonariTheme.shapes

    val fadeEnabled by AppPrefs.fadeEnabled(context).collectAsState(initial = true)
    val duckForCalls by AppPrefs.duckForCalls(context).collectAsState(initial = true)

    Column(modifier.fillMaxSize().background(colors.surfaceApp)) {
        Text(
            text = stringResource(R.string.nav_settings),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = colors.textStrong,
            modifier = Modifier.padding(start = spacing.screenEdge, end = spacing.screenEdge, top = spacing.lg, bottom = spacing.sm),
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenEdge, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SettingsSection(titleRes = R.string.settings_section_playback) {
                SettingsRow(
                    iconRes = R.drawable.ic_audio_lines,
                    titleRes = R.string.settings_fade_title,
                    subRes = R.string.settings_fade_sub,
                    isFirst = true, isLast = false,
                ) {
                    Switch(
                        checked = fadeEnabled,
                        onCheckedChange = { scope.launch { AppPrefs.setFadeEnabled(context, it) } },
                        colors = switchColors(),
                    )
                }
                SettingsRow(
                    iconRes = R.drawable.ic_phone_call,
                    titleRes = R.string.settings_duck_title,
                    subRes = R.string.settings_duck_sub,
                    isFirst = false, isLast = true,
                ) {
                    Switch(
                        checked = duckForCalls,
                        onCheckedChange = { scope.launch { AppPrefs.setDuckForCalls(context, it) } },
                        colors = switchColors(),
                    )
                }
            }

            SettingsSection(titleRes = R.string.settings_section_tile) {
                SettingsRow(
                    iconRes = R.drawable.ic_zap,
                    titleRes = R.string.settings_tile_title,
                    subRes = R.string.settings_tile_sub,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = colors.textFaint,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            SettingsSection(titleRes = R.string.settings_section_sounds) {
                SettingsRow(
                    iconRes = R.drawable.ic_folder_plus,
                    titleRes = R.string.settings_import_title,
                    subRes = R.string.settings_import_sub,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = colors.textFaint,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            SettingsSection(titleRes = R.string.settings_section_about) {
                val githubUrl = stringResource(R.string.settings_github_url)
                SettingsRow(
                    iconRes = R.drawable.ic_github,
                    titleRes = R.string.settings_github_title,
                    subRes = R.string.settings_github_sub,
                    isFirst = true, isLast = false,
                    onClick = { uriHandler.openUri(githubUrl) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_external_link),
                        contentDescription = null,
                        tint = colors.textFaint,
                        modifier = Modifier.size(18.dp),
                    )
                }

                val instagramUrl = stringResource(R.string.settings_instagram_url)
                SettingsRow(
                    iconRes = R.drawable.ic_instagram,
                    titleRes = R.string.settings_instagram_title,
                    subRes = R.string.settings_instagram_sub,
                    isFirst = false, isLast = false,
                    onClick = { uriHandler.openUri(instagramUrl) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_external_link),
                        contentDescription = null,
                        tint = colors.textFaint,
                        modifier = Modifier.size(18.dp),
                    )
                }

                val telegramUrl = stringResource(R.string.settings_telegram_url)
                SettingsRow(
                    iconRes = R.drawable.ic_send,
                    titleRes = R.string.settings_telegram_title,
                    subRes = R.string.settings_telegram_sub,
                    isFirst = false, isLast = true,
                    onClick = { uriHandler.openUri(telegramUrl) },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_external_link),
                        contentDescription = null,
                        tint = colors.textFaint,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Footer()
            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun SettingsSection(
    @StringRes titleRes: Int,
    content: @Composable () -> Unit,
) {
    val colors = SonariTheme.colors
    val spacing = SonariTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(titleRes).uppercase(java.util.Locale.getDefault()),
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = SonariSans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.12.em,
            ),
            color = colors.textFaint,
            modifier = Modifier.padding(start = 2.dp),
        )
        content()
    }
}

@Composable
private fun SettingsRow(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    @StringRes subRes: Int,
    isFirst: Boolean = true,
    isLast: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    val colors = SonariTheme.colors
    val shapes = SonariTheme.shapes
    val spacing = SonariTheme.spacing
    val shape = groupedShape(outer = 14.dp, isFirst = isFirst, isLast = isLast)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceCard)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = spacing.lg, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(shapes.sm)
                .background(colors.surfaceRaised),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = colors.accentSolid,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textStrong,
            )
            Text(
                text = stringResource(subRes),
                fontSize = 12.sp,
                color = colors.textMuted,
            )
        }
        trailing()
    }
}

@Composable
private fun Footer() {
    val colors = SonariTheme.colors
    val blanketUrl = stringResource(R.string.settings_footer_blanket_url)
    val inspired = stringResource(R.string.settings_footer_inspired)

    val annotated = buildAnnotatedString {
        val blanketStart = inspired.indexOf("Blanket")
        if (blanketStart >= 0) {
            append(inspired.substring(0, blanketStart))
            withLink(LinkAnnotation.Url(blanketUrl)) {
                withStyle(SpanStyle(color = colors.accentSolid, fontWeight = FontWeight.SemiBold)) {
                    append("Blanket")
                }
            }
            append(inspired.substring(blanketStart + 7))
        } else {
            append(inspired)
        }
    }

    Column(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = annotated,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            ),
        )
        Text(
            text = stringResource(R.string.settings_footer_license),
            fontSize = 11.sp,
            color = colors.textFaint,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private fun groupedShape(outer: Dp, isFirst: Boolean, isLast: Boolean): RoundedCornerShape {
    val top = if (isFirst) outer else 4.dp
    val bottom = if (isLast) outer else 4.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = SonariTheme.colors.surfaceApp,
    checkedTrackColor = SonariTheme.colors.accentSolid,
    checkedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
    uncheckedThumbColor = SonariTheme.colors.textFaint,
    uncheckedTrackColor = SonariTheme.colors.surfaceRaised,
    uncheckedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
)
