---
name: sonari-design
description: Use this skill to generate well-branded interfaces and assets for Sonari — an Android ambient background sound mixer ("the backdrop to your day") with a twilight, layered-dark aesthetic. Use for production UI or throwaway prototypes/mocks. Contains design guidelines, colors, type, fonts, brand assets, and a UI kit of components for prototyping.
user-invocable: true
---

Read the `readme.md` file within this skill, and explore the other available files.

If creating visual artifacts (slides, mocks, throwaway prototypes, etc), copy assets out and create static HTML files for the user to view. If working on production code, you can copy assets and read the rules here to become an expert in designing with this brand.

If the user invokes this skill without any other guidance, ask them what they want to build or design, ask some questions, and act as an expert designer who outputs HTML artifacts _or_ production code, depending on the need.

## Where things are
- `styles.css` — global CSS entry (link this; it `@import`s all tokens + fonts).
- `tokens/` — colors (twilight accent, dark + light surfaces), typography (Manrope + JetBrains Mono), spacing/radii/shadow/motion.
- `assets/` — `sonari-mark.svg`, `sonari-icon.svg` (proposed brand identity — flag for sign-off).
- `components/` — reusable React primitives, compiled to `_ds_bundle.js` under `window.SonariDesignSystem_901849`. See each `.prompt.md`.
- `guidelines/` — foundation specimen cards.
- `ui_kits/sonari-app/` — interactive Android app recreation (mixer, presets, settings, Quick Settings shade).

## Brand in one breath
Layered near-black surfaces; a single **twilight gradient** accent `#9b8cff → #ff8fb1` used **only on large elements** (play button, active sound card, slider fill, active Quick Tile); solid `#c69bff` on small icons/text. Manrope for UI, JetBrains Mono for volumes/timers. Soft, short motion; audio fades over ~1s. Sentence case, calm plain copy, no emoji. Lucide line icons. Single dark "twilight" theme — no theme switching.
