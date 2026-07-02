# Sonari — Project plan

> Name: **Sonari** (locked). Package: `io.github.liukscot.sonari`.
> Android **background sound mixer** (rain, waves, coffee, fireplace…) for
> focus, study, relaxation *and* sleep — inspired by
> [Blanket](https://github.com/rafaelmardojai/blanket) (GNOME). Not tied to
> night: the positioning is "the backdrop to your day".
> Distinctive feature (vs Blanket clones): **Quick Settings Tile** to start the
> sounds from the notification shade, without opening the app.
>
> ⚠️ **Prior art (verified June 2026):** the Quick Tile is **absent** from the
> Blanket clones (`blankee`, `Kaze`) and from the big mainstream apps, but it is
> **not unheard of**. Two apps already have it:
> - **Easy Noise** (`com.cliambrown.easynoise`) — FOSS on F-Droid, has a widget +
>   Quick Settings tile + 7 sounds. → practical reference for our Tile.
> - **Ambient Music** (`com.sourajitk.ambient_music`) — Play Store, with a QS tile.
>
> So: the Tile sets us apart from the Blanket clones and puts us at the level of
> the few apps done well — it is not a world first.

## 1. Vision

A background sound mixer. The user enables several sounds together
(rain + waves + coffee), each with its own volume, and leaves them looping
while working, studying or sleeping. It must keep playing with the screen off
and be startable/stoppable instantly from the Quick Tile.

Three pillars:
1. **Multi-mix** — several sounds at once, per-sound volume (Blanket model).
2. **Quick Settings Tile** — play/pause toggle of the last mix from the shade.
3. **Survives screen off** — Foreground Service is mandatory.

## 2. Competitive landscape and differentiation

Analysis June 2026. **Key observation:** every competitor has *either*
customization *or* the quick tile, **never both done well**. The empty niche is
the intersection.

| App | Pros | What's missing / limit |
|-----|------|------------------------|
| **Blanket** (GNOME, original) | customizable multi-mix, presets, CC0 sounds | it's Linux desktop, **doesn't exist on Android** |
| **blankee** (Android, FOSS) | good multi-mix UI, foreground service, notification | loop with click, no audio focus, **no quick tile** |
| **Kaze** (Android, FOSS) | minimal | too primitive, no real service, no tile |
| **Easy Noise** (Android, FOSS) | **has the quick tile** | unmaintained for ~4 years, very bare, too simple, few sounds, **mix not customizable** |
| **Ambient Music** (Android, Play Store) | quick tile | focused **only** on tiles, tiles **not customizable**, not a real mixer |
| **White Noise / BetterSleep** (mainstream) | many sounds, features | no quick tile, bloated, ads/paywall |

### 2.1 How we stand out (the moat)
Nobody combines **customizable mixer + configurable quick tile + a modern,
maintained app**. That's where we sit. Defining features:

1. **Customizable multi-mix** (per-sound volume) — like Blanket, better than the
   tile-only apps (Ambient Music, Easy Noise).
2. **Configurable Quick Tile** — the user picks *which* preset/mix the tile
   starts (Ambient Music has it but rigid → we don't). Optionally multiple tiles
   for different presets. ← this is the intersection nobody does.
3. **Seamless looping** (Media3 ExoPlayer) — better than blankee/Easy Noise.
4. **Robustness**: audio focus, sleep timer, fade out — missing in the bare apps.
5. **Modern and maintained**: Compose + Media3, living code (Easy Noise has been
   stalled for ~4 years).

> In one line: *"Blanket for Android, but with a quick tile you configure."*

### 2.2 Code reference: itsPronay/blankee
Android clone of Blanket (Kotlin, Compose, 25★). We read its code as a
reference. **We don't fork it** (someone else's license + package); we replicate
the good patterns and fix the flaws. (For the tile, the practical reference is
**Easy Noise** instead, see §3.4.)

To imitate:
- 1 player per sound, multi-mix = several active players with independent `setVolume`.
- `GlobalPlaybackState`: single source of truth `canPlay: StateFlow<Boolean>` +
  `togglePlayPause()`, with a **static holder** reachable from Service/Tile
  outside Compose. → this is the Tile's hook.
- Foreground Service with a media notification (play/pause + open app).
- Persistence of the last state (preset) for restart.

To fix:
- **Seamed loop**: blankee uses `MediaPlayer.isLooping`, which clicks at the loop
  point on many devices → unacceptable for sleep. → we use ExoPlayer.
- **Preloading all sounds at startup** → lazy creation on first play.
- **No audio focus** (doesn't duck for calls/other apps) → we handle it.
- **Legacy MediaSessionCompat + hand-built notification** → replaced by Media3.

## 3. Chosen architecture

```
┌─────────────────┐     toggle/intent      ┌──────────────────────────┐
│ QuickTileService │ ─────────────────────▶ │  PlaybackService          │
│ (shade)          │                        │  (Media3 MediaSessionSvc) │
└─────────────────┘                        │  - foreground + notif     │
┌─────────────────┐     observe/command     │  - audio focus            │
│  Compose UI      │ ◀────────────────────▶ │                          │
│  (sound grid)    │                        └────────────┬─────────────┘
└─────────────────┘                                      │ commands
                                              ┌───────────▼────────────┐
                                              │  AudioEngine            │
                                              │  Map<SoundId, ExoPlayer>│
                                              │  seamless loop + volume │
                                              └────────────┬───────────┘
                                                           │ persists
                                              ┌────────────▼───────────┐
                                              │ DataStore: last mix     │
                                              │ (sound ids + volumes)   │
                                              └────────────────────────┘
```

### 3.1 Audio engine — Media3 ExoPlayer
- One `ExoPlayer` per active sound, created **lazily** on first play (not all at startup).
- Seamless loop: `player.repeatMode = REPEAT_MODE_ONE`.
- Per-sound volume: `player.volume = 0f..1f`.
- `AudioAttributes(USAGE_MEDIA, CONTENT_TYPE_MUSIC)` with `handleAudioFocus = true`
  → automatically ducks/stops for calls and other apps.
- Multi-mix = N players playing together; Android mixes them at the system level.
- **Fallback if we want zero dependencies:** `MediaPlayer` (like blankee), but
  with an imperfect audible loop. Not recommended for a sleep app.

### 3.2 Playback state — single source of truth
- `canPlay: StateFlow<Boolean>` + list of active sounds with their volumes.
- Exposed both to the UI (Compose `collectAsState`) and to the Service/Tile
  (static holder or binder). The Tile has **no** state of its own: it reads this.

### 3.3 Foreground Service — Media3 `MediaSessionService`
Why it's needed: without a Foreground Service Android kills audio with the screen
off. Media3 gives almost for free:
- a media notification with play/pause (and tap → opens the app),
- foreground lifecycle handling,
- integration with headphones/Bluetooth/car.

### 3.4 Quick Settings Tile — the differentiating piece
Every `TileService` must be declared in the manifest with `BIND_QUICK_SETTINGS_TILE`.

**Two-level model:**
- **1 main tile** (`MainTileService`): play/pause toggle of the last mix.
  Core feature, Phase 2.
- **5 slot tiles** (`PresetTileService1..5`): off by default; the user adds them
  from the shade and assigns each one a preset from the app. Phase 6.

> ⚠️ **Android constraint:** tiles are NOT created at runtime — each `TileService`
> is declared statically at compile time. That's why the slots are a **fixed
> number (5)**, not an unlimited one-per-preset. It's the standard workaround for
> "multiple configurable tiles".

Main tile behavior: **play/pause toggle of the last mix**.

> 📚 **Reference to study:** `Easy Noise` (FOSS, F-Droid) already has a QS tile
> for looping sounds. Read its `TileService` + how it starts the service from the
> tile when the process is dead, before writing ours.
> Repo: search `com.cliambrown.easynoise` (GitHub/F-Droid).

Tap flow:
1. `onClick()` → sends an Intent to the `PlaybackService`
   (`startForegroundService` with action `TOGGLE`).
2. The Service checks state:
   - if playing → pause;
   - if stopped → **load the last mix from DataStore** and play.
3. The Service updates state → `onClick` updates the Tile appearance
   (`Tile.STATE_ACTIVE` / `STATE_INACTIVE`, icon, label).

**The critical point (cold start):** the Tile can be pressed when the app process
is dead and no player exists. That's why the last mix **must** be persisted in
DataStore and reloaded by the Service at startup. The persistence blankee already
has (preset) covers this case.

Edge cases to handle:
- First ever launch, no saved mix → the Tile starts a default mix
  (e.g. rain only) or opens the app the first time.
- `onStartListening()` → syncs the Tile appearance with the real state when the
  shade opens.

### 3.5 Persistence
DataStore (Preferences) with the last mix: list of `(soundId, volume)` +
play/pause flag. No Room until saved multi-presets are needed (Phase 5).

## 4. Manifest permissions
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" /> <!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" /> <!-- custom sounds, Phase 4 -->
```
- `<service android:foregroundServiceType="mediaPlayback">` for the PlaybackService.
- `<service android:permission="android.permission.BIND_QUICK_SETTINGS_TILE">`
  with an `<intent-filter>` for `android.service.quicksettings.action.QS_TILE`
  for the Tile.
- `POST_NOTIFICATIONS` must be requested at runtime on Android 13+.
- `minSdk 24` already covers the Quick Tile (API 24). OK.

## 5. UI (Compose)
- Card grid like Blanket: icon + name + volume slider under each.
  **3 columns** (4 in landscape/wide screens), scrollable.
- Active card highlighted (colored background/icon) when volume > 0.
- Bottom bar: master volume, large play/pause, menu.
- Single dark "twilight" theme, no theme switcher (matches the design system).
  Future option: a toggle to follow system dynamic colors (Material You,
  Android 12+) — see §10 "To decide later".

### 5.1 Style (working direction, changeable)
Inspired by the style of `apps/health`: **layered dark** (background ~`#070708`,
cards `#161619`→`#1e1e22`), rounded corners (10–16px), a single accent.
- **Accent = violet→pink gradient**: `#9b8cff` → `#ff8fb1`. "Twilight" mood.
- Gradient only on large elements (play, active card, slider fill, selected
  tile). On text/small icons use a **solid** tone `#c69bff` (gradients on small
  details hurt legibility).

## 6. Audio assets and licenses
- Source: repo `rafaelmardojai/blanket` → `data/resources/sounds` (14 `.ogg`).
- ⚠️ **The sounds are NOT all CC0** (the original assumption was wrong —
  verified against Blanket's `SOUNDS_LICENSING.md`). Real split:
  - **CC0:** birds, boat, stream, wind.
  - **Public Domain:** coffee-shop, fireplace, summer-night.
  - **CC BY** (attribution): city, rain, storm, train, waves.
  - **CC BY-SA** (attribution + share-alike): pink-noise, white-noise.
- **All 14 are commercially usable** — none is NonCommercial (CC BY-NC), so
  the app stays monetizable (paid/subscription). Two conditions:
  1. a **Credits screen** must attribute the 7 CC BY / CC BY-SA sounds
     (`assets/sound_credits.md` holds the data; screen lands in Phase 1c);
  2. the 2 CC BY-SA files keep their license, and CC BY-SA's anti-DRM clause
     means no store DRM on top (F-Droid is clean; Play paid is fine without
     added DRM). Swap pink/white noise for CC0 later if a DRM model is chosen.
- Looping files, `.ogg` format. Bundled in `res/raw/` (lowercase + underscore
  names, e.g. `coffee_shop.ogg`).

## 7. Migration from the current template
The current state was the Compose template with a "soundboard" using `SoundPool`.
- ✅ Removed `SoundPool` from `MainActivity` (wrong for long loops) — now a placeholder.
- ✅ Renamed package `com.example.myapplication` → see §8.
- Kept: Compose structure, `Theme`/`Type`/`Color`, Gradle/Compose BOM.

## 8. Move, rename, git — ✅ DONE (initial commit)
- Moved to `~/github/apps/sonari/`.
- `git init` + first commit (personal identity).
- Package/namespace/applicationId → `io.github.liukscot.sonari`; theme →
  `SonariTheme` / `Theme.Sonari`; `app_name` + rootProject → "Sonari";
  source folders moved to `io/github/liukscot/sonari/`.
- `.gitignore` extended (build, .gradle, .kotlin, .claude-flow).

## 9. Phased roadmap
- **Phase 0 — setup**: ✅ DONE — moved to apps/, git init, package rename.
- **Phase 1 — audio MVP**: AudioEngine (Media3 ExoPlayer multi-loop, 15 sounds) +
  grid UI with per-sound sliders + master volume + fade in/out + 3 themes
  (light/dark/system). Plays while the app is open. *(no service/tile yet)*
- **Phase 2 — background + Quick Tile**: PlaybackService (Media3) + notification +
  audio focus + last-mix persistence (DataStore) + `TileService` with toggle of
  the last mix + cold start. Plays with screen off. **The requested feature.**
- **Phase 3 — sleep timer**: stop after N minutes (optional, default = infinite).
- **Phase 4 — custom sounds**: import user audio files (`READ_MEDIA_AUDIO` +
  add/remove UI).
- **Phase 5 — presets**: save/load named mixes (Room), like "afterRain".
- **Phase 6 — configurable Quick Tile**: the user picks which preset the tile
  starts (optionally multiple tiles for different presets). ← **the moat vs the
  competition** (§2.1).

## 10. Decisions made
- [x] **Name + package** → Sonari / `io.github.liukscot.sonari`.
- [x] **Positioning** → background sound mixer (focus/study/relax/sleep).
- [x] **Publishing** → open code on GitHub; F-Droid free; Play Store paid
      (subscription vs one-time model: *decided later*).
- [x] **License** → GPLv3 (copyleft, like Blanket).
- [x] **UI language** → English (for store reach).
- [x] **Audio engine** → Media3 ExoPlayer.
- [x] **Sound set** → all 14 sounds from Blanket at launch (mixed CC0 / PD /
      CC BY / CC BY-SA — all commercial-OK, attribution required; see §6).
- [x] **Custom sounds** → yes, supported early (Phase 4, after the timer).
- [x] **Sleep timer** → yes (Phase 3), optional, default = infinite playback.
- [x] **Theme** → single dark "twilight" theme, no switcher (design-system decision).
      Dynamic system colors (Material You) deferred — see "To decide later".
- [x] **Volume** → per-sound + master.
- [x] **Auto-resume on boot** → no.
- [x] **Fade in/out** → yes (~1s on play/pause).
- [x] **minSdk** → 24 (Android 7).
- [x] **Grid layout** → 3 columns (4 in landscape).
- [x] **UI style** → layered dark (`apps/health` style), violet→pink gradient
      accent `#9b8cff`→`#ff8fb1` (solid `#c69bff` on small details).
- [x] **Presets** → the user saves their own mixes + 2-3 default presets.
- [x] **Quick Tile** → 1 main tile (toggle last mix) + 5 slot tiles assignable to
      presets, off by default (constraint: fixed count).

## 11. Material3 component audit (issue #32)

Goal: replace hand-rolled `Box`/`Modifier.clickable` widgets with stock M3
components where it doesn't compromise the "twilight" look. Decision recorded
here so it isn't re-litigated.

**Converted (pixel-identical, zero visual loss):**
- `MixerScreen.kt` — edit-layout and sleep-timer circular buttons →
  `FilledIconButton`; "Add group" and both "Cancel" buttons → `FilledTonalButton`;
  the 28dp rename/delete mini-buttons on group headers → `FilledIconButton`.
- `PresetsScreen.kt` — `NewPresetButton` → `OutlinedButton`; the preset
  play/pause circle → `FilledIconButton`.
- Win: correct M3 ripple/semantics/disabled-state for free, same colors/shape/size
  as before (all passed explicitly via `colors =`/`shape =`/`Modifier.size`).

**Converted despite risk (accepted 2026-07-02) — verify on-device before closing
the issue, not yet checked in an emulator:**
- `SonariNavBar`/`NavBarItem` (`SonariApp.kt`) → `NavigationBar`/`NavigationBarItem`,
  `indicatorColor = Color.Transparent` to keep the no-pill look, forced back to
  the compact 62dp height via `Modifier.height()`. Two follow-up fixes needed
  after the first pass: (1) fixing the height on the whole component fought
  with M3's own gesture-bar inset padding (the system's gesture pill rendered
  on top of the "Presets" label) — fixed by disabling `NavigationBar`'s
  internal `windowInsets` (`WindowInsets(0.dp)`) and re-adding the inset
  padding on an outer `Box` instead; (2) that outer `Box` had no background,
  so the inset-padding strip at the very bottom showed the screen's near-black
  `surfaceApp` instead of the card's `surfaceCard` gray — fixed by moving
  `clip(navShape)` + `background(colors.surfaceCard)` onto the outer `Box`
  (same structure the old custom Column/Row had: paint the card color first,
  inset-pad the content after).
- `SettingsRow` (`SettingsScreen.kt`) and `PresetMainRow`/`PresetTileRow`
  (`PresetsScreen.kt`) → M3 `ListItem` (leading/headline/supporting/trailing
  slots). **Known side-effect:** the stable `ListItem` API has no
  `contentPadding` override, so internal row padding now follows M3's own
  token instead of the app's `spacing.lg`/16dp — row height/rhythm may shift
  slightly from before.

**Kept custom on purpose (M3 stock components can't reproduce them):**
- The edit-mode "Done" button and the rename-sheet confirm button paint the
  `accentGradient` **Brush**; `SoundCard`'s tile fill uses the same gradient.
  Stock `Button`/`IconButton` only accept a solid `Color` for their container,
  not a `Brush` — converting would strip the gradient, the app's single most
  identity-carrying visual (see §5.1).
- Play/Pause button: kept as a custom gradient `Box`, but now **morphs shape**
  like Material's media controls — `RoundedCornerShape` corner radius animates
  between a full circle (paused) and a 20dp squircle (playing) via
  `animateDpAsState`, so it behaves like a Material play/pause toggle without
  losing the gradient.
- `SonariSlider.kt` — the track fill is also a gradient; M3's customizable
  `Slider` track/thumb slots would still need a hand-drawn gradient underneath,
  so converting buys little.

**Not converted, simply not gotten to yet:**
- Icon-picker chips in `NewPresetDialog` — reasonable `FilterChip` candidates.

### To decide later
- [ ] Play Store paid model (subscription vs one-time cost).
- [ ] App icon / branding (currently the default).
- [ ] Optional "follow system colors" toggle (Material You dynamic color,
      Android 12+) as an alternative to the fixed twilight theme.
```
