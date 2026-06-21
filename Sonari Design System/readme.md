# Sonari — Design System

**Sonari** is an ambient **background sound mixer** for Android. You enable
several looping sounds at once — rain + waves + a quiet café — each with its own
volume, and leave them playing while you work, study, relax or sleep. Its
positioning is *"the backdrop to your day"* — not tied to night.

Inspired by the GNOME app **Blanket**, Sonari's headline differentiator is a
**configurable Quick Settings tile**: start/stop a saved mix straight from the
Android notification shade, without opening the app. No Blanket clone combines a
customizable mixer *and* a configurable quick tile — that intersection is the
product's reason to exist.

- **Platform:** Android (Kotlin · Jetpack Compose · Media3 / ExoPlayer · minSdk 24)
- **License:** GPL-3.0. Built-in sounds are CC0 (sourced from Blanket).
- **Status:** early development — the codebase is currently the Compose
  scaffold; the visual direction below is taken from the project's design spec.

## Sources used to build this system
- **GitHub — app codebase:** https://github.com/LiukScot/sonari
  (read `PLAN.md` for the full product spec & UI direction; `README.md` for the
  pitch; `app/src/main/java/.../ui/theme/` for the current Compose theme).
- **Design lineage — Blanket (GNOME):** https://github.com/rafaelmardojai/blanket
  (the multi-mix model and the CC0 sound set).

> Explore the Sonari repo above to go deeper — the `PLAN.md` file is the
> authoritative spec for features, architecture and the intended visual style.

⚠️ **What was inferred, and needs your sign-off:**
- **No real logo or app icon exists yet** (the repo still ships the default
  Android template icon). The twilight **soundwave mark / wordmark / app icon**
  in `assets/` are a *proposed* identity built from the documented "twilight"
  direction. Please confirm or replace.
- **Brand font:** the app uses the Android system font (Roboto). We propose
  **Manrope** (Google Fonts) as the brand UI font for a calmer, rounded feel.
  This is a substitution — swap it if you have a preferred typeface.
- **Icons:** **Lucide** (CDN) is used as the sound/UI icon set (clean ~2px line
  icons). The repo has no icon set of its own, so this is a substitution.

---

## CONTENT FUNDAMENTALS

**Voice — calm, plain, unhurried.** Copy sounds like the app feels: quiet and
warm. Short lines, no hype, no exclamation marks. The product fades into the
background, and so does its language.

- **Person:** address the user as **you**; the app refers to itself as *Sonari*
  or just *we* sparingly. Imperative for actions ("Build your mix", "Tap to
  start", "Save a mix").
- **Casing:** **Sentence case** everywhere — titles, buttons, settings rows.
  Never Title Case UI. The wordmark "Sonari" and proper nouns (Quick Settings,
  Android) are the only capitals.
- **Tone words:** *soft, looping, backdrop, mix, quiet, drift, fade.* Sensory,
  not technical. Describe sounds by feeling ("Rain, waves and a quiet café,
  looping softly").
- **Tagline:** **"The backdrop to your day."** Use as the app subtitle.
- **Numbers & units:** lower-case, spaced naturally — "3 sounds mixing",
  "15 min", "1 hour", "~1s fade". Volumes and timers are shown in the **mono**
  face.
- **No emoji** in product UI. (Emoji appear only in internal docs like PLAN.md.)
- **Feature names:** *mix*, *preset*, *Quick Settings tile* (capital Q/S as it's
  an Android surface), *sleep timer*, *master volume*.
- **Examples of good copy:**
  - "The backdrop to your day"
  - "Save a mix, then start it from the shade."
  - "Fade out and stop after…" (sleep timer)
  - "Plays with the screen off"
  - "Lower audio for calls & other apps" (audio-focus setting)

---

## VISUAL FOUNDATIONS

The mood is **"Twilight"** — a layered, near-black interface lit by a single
violet→pink accent. Restraint is the rule: one accent, one gradient, used only
where it earns attention.

**Color.** The system ships a **single dark "twilight" theme** (no theme
switching). Surfaces are layered greys from `#0e0e10` (void) up
through `#161619` (card) to `#26262b` (pressed). The brand accent is the
**twilight gradient** `#9b8cff → #ff8fb1`. **Gradient rule (from the spec):** the
gradient appears *only on large elements* — the master play button, an active
sound card, slider fills, a selected/active Quick Tile. On small details, icons
and text use the **solid** accent `#c69bff` (gradients on small things hurt
legibility). Semantic colors are kept quiet and twilight-toned.

**Type.** **Manrope** (proposed) for all UI — calm rounded geometric sans;
weights 400–800. Display/titles use tight tracking (`-0.02em`) and heavy weights
(700–800). **JetBrains Mono** for anything numeric — volumes, timers, token
values — which gives the mixer a precise, instrument-like feel.

**Spacing & layout.** 4px base grid. Screen gutters 16–18px. The **sound grid is
3 columns** (4 in landscape), 12px gaps. A fixed **bottom playback bar** (master
volume + large play/pause) is rounded at the top and floats above the grid with
an upward shadow. Min tap target 44px.

**Corners.** Rounded, soft: chips/inputs 10px, **sound cards 14px**, sheets &
dialogs 18px, the bottom bar 24px, and **pill** (999px) for the play button,
sliders and segmented controls. Nothing is sharp-cornered.

**Cards.** A resting sound card is a flat `#161619` surface with a 10%-white
hairline border and a barely-there shadow. When **active** (volume > 0) it
swaps to a top-lit gradient surface (`#1e1e22 → #161619`), gains a **gradient
hairline ring** and a soft **accent glow** (`0 8px 28px rgba(155,140,255,.32)`),
and its icon tints to the accent. This active-lit state is the system's
signature move.

**Shadows & glow.** Two families: soft black **elevation** shadows (sm/md/lg,
low and diffuse, tuned for dark) and the **accent glow** reserved for the two
elements that carry the full gradient — the play button and the active card/tile.
Never glow a resting element.

**Transparency & blur.** Used only for overlays: the notification **shade** and
sheet scrims use a translucent near-black with a light backdrop-blur. UI surfaces
themselves are solid (legibility first).

**Backgrounds.** No photography, no illustration, no texture inside the app —
the layered greys *are* the background. The only gradient surfaces are the
accent ones. (The marketing/showcase frame sits on a subtle radial twilight
glow, but that's chrome, not the app.)

**Motion.** Gentle and short. Default easing `cubic-bezier(.22,1,.36,1)`
(ease-out); a soft overshoot `cubic-bezier(.34,1.2,.64,1)` on toggles, the play
button press and sheets. Durations: 140ms (hover/press), 220ms (card activate),
420ms (sheets/shade). Audio itself **fades in/out over ~1s** on play/pause — the
defining "soft" behaviour. No infinite/decorative loops.

**Press & hover states.** Press = **scale down** (0.90–0.98) with the soft-
overshoot easing; the play button dips to 0.93. Hover (where a pointer exists)
lifts borders from 10%→16% white and brightens surfaces one step. Active controls
tint to the accent or fill with the gradient.

**Borders.** Hairlines only — `rgba(255,255,255,.06)` dividers, `.10` card edges,
`.16` on hover.

---

## ICONOGRAPHY

Sonari has **no bespoke icon set** in the codebase, so the system standardises on
**Lucide** (https://lucide.dev) — open-source, ~2px line icons with rounded caps
that match the calm, soft aesthetic. *(Substitution — flagged; swap if you adopt
another set.)*

- **Style:** outline / line icons, never filled or duotone. Stroke ~2px.
- **One icon per sound** (the grid's vocabulary):
  rain `cloud-rain`, storm `cloud-lightning`, wind `wind`, waves `waves`,
  stream `droplet`, birds `bird`, summer night `moon-star`, crickets `bug`,
  train `train-front`, boat `sailboat`, city `building-2`, coffee shop `coffee`,
  fireplace `flame`, white noise `audio-waveform`, pink noise `radio`.
- **UI icons:** `play` / `pause`, `volume-2`, `timer` / `moon` (sleep), `zap`
  (Quick Tile), `settings`, `sliders-horizontal`, `layers`, `plus`.
- **Color:** muted (`--text-muted`) at rest; **solid accent** `#c69bff` when
  active or emphasised — never the gradient on a small icon.
- **No emoji, no unicode glyphs** as icons in product UI.
- **Brand mark:** the Sonari **soundwave** — a source dot with three radiating
  arcs in the twilight gradient. Files in `assets/` (`sonari-mark.svg`,
  `sonari-icon.svg`). These are pure-geometry SVGs (proposed identity).

**How to load Lucide** in a card or kit:
```html
<script src="https://unpkg.com/lucide@0.456.0/dist/umd/lucide.min.js"></script>
<i data-lucide="cloud-rain"></i>
<script>lucide.createIcons()</script>
```
Sonari components render `<i data-lucide="…">` internally — the host page must
call `lucide.createIcons()` after React renders (and on updates).

---

## Using this system

- **Global CSS:** link the single entry point **`styles.css`** — it `@import`s
  `tokens/colors.css`, `tokens/typography.css`, `tokens/spacing.css` (which also
  pulls Manrope + JetBrains Mono from Google Fonts).
- **Components** (compiled to `_ds_bundle.js`, namespace
  `window.SonariDesignSystem_901849`): mount in plain HTML via
  `const { Button } = window.SonariDesignSystem_901849`.
- **Single dark theme.** The system is dark-only ("twilight") — there is no
  theme switcher.

---

## Index / manifest

**Root**
- `styles.css` — global entry (import this).
- `readme.md` — this guide.
- `SKILL.md` — portable skill manifest (for Claude Code / Agent Skills).

**`tokens/`** — `colors.css` (twilight accent, dark + light surfaces, text,
semantic), `typography.css` (Manrope/Mono families, scale, weights),
`spacing.css` (4px scale, radii, shadow/glow, motion, layout knobs).

**`assets/`** — `sonari-mark.svg` (soundwave mark), `sonari-icon.svg` (app icon).
*Proposed identity — pending your sign-off.*

**`components/`** — reusable primitives (namespace
`SonariDesignSystem_901849`):
- `core/` — **Button**, **IconButton**, **PlayButton**, **VolumeSlider**,
  **Switch**, **SegmentedControl**, **Badge**.
- `sound/` — **SoundCard** (the signature mixer tile) and **QuickTile** (the
  Android Quick Settings tile — the differentiator).
- Each has a `.d.ts` (props), `.prompt.md` (usage), and a directory
  `*.card.html` showcase (Design System tab).

**`guidelines/`** — foundation specimen cards (Colors, Type, Spacing, Brand).

**`ui_kits/sonari-app/`** — interactive recreation of the Android app:
`index.html` (phone frame + bottom nav + shade), `MixerScreen.jsx`,
`PresetsScreen.jsx`, `SettingsScreen.jsx`, `ShadeScreen.jsx`, `data.js`.

---

*Built from the Sonari repo and Blanket lineage cited above. Where the codebase
was only a scaffold, the documented `PLAN.md` direction was followed and every
inferred choice (logo, font, icons) is flagged for your review.*
