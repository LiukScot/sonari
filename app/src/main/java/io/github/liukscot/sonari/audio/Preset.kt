package io.github.liukscot.sonari.audio

import java.util.UUID

data class Preset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconName: String,
    val volumes: Map<String, Float>,
    val masterVolume: Float,
    val tileEnabled: Boolean = false,
)

val PRESET_ICON_OPTIONS = listOf(
    "cloud-rain", "waves", "wind", "moon-star",
    "flame", "coffee", "bird", "droplet",
)
