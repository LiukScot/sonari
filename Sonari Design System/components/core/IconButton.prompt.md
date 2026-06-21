One-line: A round, quiet icon button for top bars and the playback bar.

```jsx
<IconButton icon="moon" aria-label="Sleep timer" />
<IconButton icon="heart" active tone="raised" />
```

`tone="default"` is transparent (tints to accent when `active`); `tone="raised"` sits on a dark surface. Host must run `lucide.createIcons()`.
