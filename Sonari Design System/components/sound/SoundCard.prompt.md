One-line: The signature Sonari mixer tile — tap to toggle a sound on/off, drag the slider for its volume; lights up with the twilight gradient when active.

```jsx
<SoundCard name="Rain" icon="cloud-rain" volume={62}
  onToggle={() => …} onVolume={(v) => …} />
```

Active state (`volume > 0`) swaps the flat surface for the card gradient, adds a gradient hairline ring + accent glow, and tints the icon. Lay these out in a 3-column grid (4 wide on landscape). Host must run `lucide.createIcons()` after render so the `icon` shows.
