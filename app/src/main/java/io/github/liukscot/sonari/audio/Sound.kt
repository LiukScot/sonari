package io.github.liukscot.sonari.audio

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import io.github.liukscot.sonari.R

/* One built-in sound. iconName is the Lucide id from the design system; the
   grid (Phase 1c) maps it to a vector drawable. Licensing lives in
   assets/sound_credits.md, not here. */
data class Sound(
    val id: String,
    @param:StringRes val nameRes: Int,
    @param:RawRes val resId: Int,
    val iconName: String,
    @param:StringRes val categoryRes: Int,
)

/* Sounds are listed in category order so grouping by categoryRes keeps the
   intended section order (Nature, Places, Static noise). */
val BUILT_IN_SOUNDS: List<Sound> = listOf(
    Sound("rain", R.string.sound_rain, R.raw.rain, "cloud-rain", R.string.category_nature),
    Sound("storm", R.string.sound_storm, R.raw.storm, "cloud-lightning", R.string.category_nature),
    Sound("wind", R.string.sound_wind, R.raw.wind, "wind", R.string.category_nature),
    Sound("waves", R.string.sound_waves, R.raw.waves, "waves", R.string.category_nature),
    Sound("stream", R.string.sound_stream, R.raw.stream, "droplet", R.string.category_nature),
    Sound("birds", R.string.sound_birds, R.raw.birds, "bird", R.string.category_nature),
    Sound("summer_night", R.string.sound_summer_night, R.raw.summer_night, "moon-star", R.string.category_nature),
    Sound("fireplace", R.string.sound_fireplace, R.raw.fireplace, "flame", R.string.category_nature),
    Sound("train", R.string.sound_train, R.raw.train, "train-front", R.string.category_places),
    Sound("boat", R.string.sound_boat, R.raw.boat, "sailboat", R.string.category_places),
    Sound("city", R.string.sound_city, R.raw.city, "building-2", R.string.category_places),
    Sound("coffee_shop", R.string.sound_coffee_shop, R.raw.coffee_shop, "coffee", R.string.category_places),
    Sound("white_noise", R.string.sound_white_noise, R.raw.white_noise, "audio-waveform", R.string.category_ambient),
    Sound("pink_noise", R.string.sound_pink_noise, R.raw.pink_noise, "radio", R.string.category_ambient),
)
