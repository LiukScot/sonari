One-line: Pill segmented control for exclusive choices — theme (light/dark/system), section tabs.

```jsx
<SegmentedControl
  options={[{value:'light',icon:'sun'},{value:'dark',icon:'moon'},{value:'system',icon:'smartphone'}]}
  value={theme} onChange={setTheme} />
```

Selected segment fills with the twilight gradient. Host must run `lucide.createIcons()` when options have icons.
