import React from 'react';

/** VolumeSlider — horizontal slider with twilight-gradient fill. */
export function VolumeSlider({
  value = 50, icon, active = true, onChange, label, style = {},
}) {
  const track = {
    position: 'relative', flex: 1, height: 8, borderRadius: 'var(--r-pill)',
    background: 'var(--surface-pressed)', cursor: 'pointer',
  };
  const fill = {
    position: 'absolute', left: 0, top: 0, bottom: 0, width: `${value}%`,
    borderRadius: 'var(--r-pill)',
    background: active ? 'var(--accent-gradient)' : 'var(--text-faint)',
    transition: 'width var(--dur-fast) var(--ease-out)',
  };
  const knob = {
    position: 'absolute', top: '50%', left: `${value}%`,
    transform: 'translate(-50%, -50%)', width: 18, height: 18, borderRadius: '50%',
    background: '#fff', boxShadow: '0 2px 6px rgba(0,0,0,0.5)', pointerEvents: 'none',
  };
  const set = (e) => {
    if (!onChange) return;
    const r = e.currentTarget.getBoundingClientRect();
    onChange(Math.round(Math.max(0, Math.min(100, ((e.clientX - r.left) / r.width) * 100))));
  };
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--sp-3)', ...style }}>
      {icon && <i data-lucide={icon} style={{ width: 20, height: 20, color: 'var(--text-muted)', flex: '0 0 auto' }} />}
      <div style={track} onClick={set}>
        <div style={fill} />
        <div style={knob} />
      </div>
      {label && <span style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--t-caption)',
        color: 'var(--text-muted)', width: 34, textAlign: 'right' }}>{value}</span>}
    </div>
  );
}
