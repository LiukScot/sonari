import React from 'react';

/** Badge / Tag — small status or category pill. */
export function Badge({ children, variant = 'neutral', icon, style = {} }) {
  const variants = {
    neutral: { background: 'var(--surface-raised)', color: 'var(--text-muted)', border: 'var(--border-default)' },
    accent:  { background: 'var(--accent-016)', color: 'var(--accent-solid)', border: 'transparent' },
    gradient:{ background: 'var(--accent-gradient)', color: 'var(--text-on-accent)', border: 'transparent' },
    success: { background: 'rgba(127,214,166,0.16)', color: 'var(--success)', border: 'transparent' },
    outline: { background: 'transparent', color: 'var(--text-body)', border: 'var(--border-default)' },
  };
  const v = variants[variant] || variants.neutral;
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 5,
      height: 24, padding: '0 10px', borderRadius: 'var(--r-pill)',
      fontFamily: 'var(--font-sans)', fontSize: 'var(--t-micro)', fontWeight: 600,
      letterSpacing: '0.02em', background: v.background, color: v.color,
      border: `1px solid ${v.border}`, whiteSpace: 'nowrap', ...style,
    }}>
      {icon && <i data-lucide={icon} style={{ width: 13, height: 13 }} />}
      {children}
    </span>
  );
}
