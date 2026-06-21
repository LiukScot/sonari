One-line: Horizontal volume slider with the twilight-gradient fill — used for master volume and inside sound cards.

```jsx
<VolumeSlider value={70} icon="volume-2" label onChange={setVol} />
```

`active={false}` mutes the fill to grey (e.g. when the mix is paused). Host must run `lucide.createIcons()` if `icon` is set.
