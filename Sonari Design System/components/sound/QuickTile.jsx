import React from 'react';

/**
 * QuickTile — a recreation of an Android Quick Settings tile, Sonari's
 * signature differentiator. Active tiles fill with the twilight gradient
 * (Android's accent-fill convention); inactive tiles are an outlined chip.
 */
export function QuickTile({
  label = 'Sonari', sub, icon = 'audio-lines', active = false, onClick, style = {},
}) {
  const wrap = {
    display: 'flex', alignItems: 'center', gap: 'var(--sp-3)',
    minWidth: 150, height: 56, padding: '0 16px', borderRadius: 'var(--r-xl)',
    cursor: 'pointer', WebkitTapHighlightColor: 'transparent',
    background: active ? 'var(--accent-gradient)' : 'var(--surface-raised)',
    border: `1px solid ${active ? 'transparent' : 'var(--border-hover)'}`,
    boxShadow: active ? 'var(--glow-accent)' : 'none',
    transition: 'all var(--dur-base) var(--ease-out)', ...style,
  };
  const ic = {
    width: 38, height: 38, borderRadius: '50%', display: 'grid', placeItems: 'center',
    flex: '0 0 auto',
    background: active ? 'rgba(26,20,48,0.18)' : 'var(--surface-pressed)',
    color: active ? 'var(--text-on-accent)' : 'var(--text-muted)',
  };
  return (
    <div style={wrap} onClick={onClick}>
      <div style={ic}><i data-lucide={icon} style={{ width: 20, height: 20 }} /></div>
      <div style={{ display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        <span style={{ fontFamily: 'var(--font-sans)', fontSize: 'var(--t-label)', fontWeight: 700,
          color: active ? 'var(--text-on-accent)' : 'var(--text-strong)',
          whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{label}</span>
        <span style={{ fontFamily: 'var(--font-sans)', fontSize: 'var(--t-micro)', fontWeight: 500,
          color: active ? 'rgba(26,20,48,0.7)' : 'var(--text-faint)',
          whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {sub || (active ? 'Playing' : 'Tap to start')}</span>
      </div>
    </div>
  );
}
