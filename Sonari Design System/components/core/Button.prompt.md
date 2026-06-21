One-line: Sonari's pill-shaped action button — `primary` carries the twilight gradient and should appear at most once per view.

```jsx
<Button variant="primary" icon="play">Play mix</Button>
<Button variant="secondary" icon="shuffle">Shuffle</Button>
<Button variant="ghost">Skip</Button>
```

Variants: `primary` (gradient + glow), `secondary` (raised dark surface, hairline border), `ghost` (accent text), `danger` (red outline). Sizes: `sm` / `md` / `lg`. Pass `icon` / `iconRight` as Lucide names; the host page must run `lucide.createIcons()` after render. `full` stretches to container width.
