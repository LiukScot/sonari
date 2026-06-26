package io.github.liukscot.sonari.ui

import androidx.annotation.DrawableRes
import io.github.liukscot.sonari.R

@DrawableRes
fun soundIconRes(iconName: String): Int = when (iconName) {
    "cloud-rain"      -> R.drawable.ic_cloud_rain
    "cloud-lightning" -> R.drawable.ic_cloud_lightning
    "wind"            -> R.drawable.ic_wind
    "waves"           -> R.drawable.ic_waves
    "droplet"         -> R.drawable.ic_droplet
    "bird"            -> R.drawable.ic_bird
    "moon-star"       -> R.drawable.ic_moon_star
    "train-front"     -> R.drawable.ic_train_front
    "sailboat"        -> R.drawable.ic_sailboat
    "building-2"      -> R.drawable.ic_building_2
    "coffee"          -> R.drawable.ic_coffee
    "flame"           -> R.drawable.ic_flame
    "audio-waveform"  -> R.drawable.ic_audio_waveform
    "radio"           -> R.drawable.ic_radio
    else              -> error("No drawable mapped for icon '$iconName'")
}
