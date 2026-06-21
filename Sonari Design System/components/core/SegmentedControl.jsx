import React from 'react';

/** SegmentedControl — pill row for exclusive choices (e.g. theme, tabs). */
export function SegmentedControl({ options = [], value, onChange, style = {} }) {
  const norm = options.map((o) => (typeof o === 'string' ? { value: o, label: o } : o));
  const current = value != null ? value : norm[0] && norm[0].value;
  return (
    <div style={{
      display: 'inline-flex', padding: 4, gap: 2, borderRadius: 'var(--r-pill)',
      background: 'var(--surface-raised)', border: '1px solid var(--border-default)', ...style,
    }}>
      {norm.map((o) => {
        const sel = o.value === current;
        return (
          <button key={o.value} type="button"
            onClick={() => onChange && onChange(o.value)}
            style={{
              display: 'inline-flex', alignItems: 'center', gap: 6,
              height: 34, padding: '0 16px', border: 'none', cursor: 'pointer',
              borderRadius: 'var(--r-pill)', fontFamily: 'var(--font-sans)',
              fontSize: 'var(--t-caption)', fontWeight: 600,
              color: sel ? 'var(--text-on-accent)' : 'var(--text-muted)',
              background: sel ? 'var(--accent-gradient)' : 'transparent',
              boxShadow: sel ? 'var(--glow-accent)' : 'none',
              transition: 'all var(--dur-fast) var(--ease-out)',
              WebkitTapHighlightColor: 'transparent',
            }}>
            {o.icon && <i data-lucide={o.icon} style={{ width: 15, height: 15 }} />}
            {o.label}
          </button>
        );
      })}
    </div>
  );
}
