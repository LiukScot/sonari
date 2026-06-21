One-line: A recreation of an Android Quick Settings tile — Sonari's headline feature, used in the shade view and tile-config screens.

```jsx
<QuickTile label="afterRain" icon="cloud-drizzle" active />
<QuickTile label="Add preset" icon="plus" />
```

Active tiles fill with the twilight gradient (Android's accent-fill convention); inactive are outlined chips. Host must run `lucide.createIcons()`.
