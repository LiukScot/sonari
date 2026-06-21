import React from 'react';

/**
 * SoundCard — Sonari's signature tile. Icon + name + volume slider.
 * When active (volume > 0) it lights up with the twilight gradient ring
 * and a soft glow, matching PLAN §5 ("active card highlighted").
 */
export function SoundCard({
  name = 'Rain',
  icon = 'cloud-rain',
  volume = 0,            // 0..100
  active,                // optional override; defaults to volume > 0
  onToggle,
  onVolume,
  style = {},
}) {
  const isActive = active != null ? active : volume > 0;

  const wrap = {
    position: 'relative', display: 'flex', flexDirection: 'column',
    gap: 'var(--sp-3)', padding: 'var(--card-pad)', borderRadius: 'var(--r-md)',
    background: isActive ? 'var(--surface-card-grad)' : 'var(--surface-card)',
    border: `1px solid ${isActive ? 'transparent' : 'var(--border-default)'}`,
    boxShadow: isActive ? 'var(--glow-accent)' : 'var(--shadow-sm)',
    cursor: 'pointer', userSelect: 'none', WebkitTapHighlightColor: 'transparent',
    transition: 'background var(--dur-base) var(--ease-out), box-shadow var(--dur-base) var(--ease-out), transform var(--dur-fast) var(--ease-out)',
    overflow: 'hidden',
    ...style,
  };
  // gradient hairline ring when active
  const ring = isActive ? {
    position: 'absolute', inset: 0, borderRadius: 'var(--r-md)', padding: 1,
    background: 'var(--accent-gradient)', WebkitMask:
      'linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0)',
    WebkitMaskComposite: 'xor', maskComposite: 'exclude', pointerEvents: 'none',
  } : { display: 'none' };

  const iconWrap = {
    width: 42, height: 42, borderRadius: 'var(--r-sm)',
    display: 'grid', placeItems: 'center',
    background: isActive ? 'var(--accent-016)' : 'var(--surface-raised)',
    color: isActive ? 'var(--accent-solid)' : 'var(--text-muted)',
    transition: 'all var(--dur-base) var(--ease-out)',
  };

  return (
    <div
      style={wrap}
      onClick={() => onToggle && onToggle()}
      onMouseDown={(e) => (e.currentTarget.style.transform = 'scale(0.98)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
    >
      <div style={ring} />
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={iconWrap}><i data-lucide={icon} style={{ width: 22, height: 22 }} /></div>
        <span style={{
          fontFamily: 'var(--font-mono)', fontSize: 'var(--t-micro)', fontWeight: 600,
          color: isActive ? 'var(--accent-solid)' : 'var(--text-faint)',
          letterSpacing: '0.02em',
        }}>{isActive ? `${volume}` : 'OFF'}</span>
      </div>
      <div style={{
        fontFamily: 'var(--font-sans)', fontSize: 'var(--t-label)', fontWeight: 600,
        color: isActive ? 'var(--text-strong)' : 'var(--text-body)',
      }}>{name}</div>
      <Slider value={volume} active={isActive} onChange={onVolume} />
    </div>
  );
}

function Slider({ value = 0, active, onChange }) {
  const track = {
    position: 'relative', height: 6, borderRadius: 'var(--r-pill)',
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
    transform: 'translate(-50%, -50%)', width: 14, height: 14,
    borderRadius: '50%', background: '#fff',
    boxShadow: '0 1px 4px rgba(0,0,0,0.5)', pointerEvents: 'none',
    opacity: active ? 1 : 0.4,
  };
  return (
    <div
      style={track}
      onClick={(e) => {
        if (!onChange) return;
        const r = e.currentTarget.getBoundingClientRect();
        onChange(Math.round(Math.max(0, Math.min(100, ((e.clientX - r.left) / r.width) * 100))));
      }}
    >
      <div style={fill} />
      <div style={knob} />
    </div>
  );
}
