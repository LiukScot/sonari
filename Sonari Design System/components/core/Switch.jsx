import React from 'react';

/** Switch — on/off toggle. Twilight gradient track when on. */
export function Switch({ checked = false, disabled = false, onChange, style = {} }) {
  const track = {
    width: 46, height: 28, borderRadius: 'var(--r-pill)', padding: 3,
    background: checked ? 'var(--accent-gradient)' : 'var(--surface-pressed)',
    border: `1px solid ${checked ? 'transparent' : 'var(--border-default)'}`,
    cursor: disabled ? 'not-allowed' : 'pointer', opacity: disabled ? 0.45 : 1,
    transition: 'background var(--dur-base) var(--ease-out)',
    display: 'flex', alignItems: 'center',
    justifyContent: checked ? 'flex-end' : 'flex-start',
    WebkitTapHighlightColor: 'transparent', ...style,
  };
  const knob = {
    width: 20, height: 20, borderRadius: '50%', background: '#fff',
    boxShadow: '0 1px 3px rgba(0,0,0,0.4)',
    transition: 'all var(--dur-base) var(--ease-soft)',
  };
  return (
    <button type="button" role="switch" aria-checked={checked} disabled={disabled}
      style={track} onClick={() => !disabled && onChange && onChange(!checked)}>
      <div style={knob} />
    </button>
  );
}
