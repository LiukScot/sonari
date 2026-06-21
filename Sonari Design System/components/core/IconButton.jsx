import React from 'react';

/** IconButton — round, quiet icon control for toolbars and bars. */
export function IconButton({
  icon = 'menu', size = 'md', tone = 'default', active = false, disabled = false,
  onClick, style = {}, 'aria-label': ariaLabel, ...rest
}) {
  const sizes = { sm: 36, md: 44, lg: 52 };
  const d = sizes[size] || 44;
  const tones = {
    default: { color: active ? 'var(--accent-solid)' : 'var(--text-muted)',
               background: active ? 'var(--accent-016)' : 'transparent' },
    raised:  { color: 'var(--text-strong)', background: 'var(--surface-raised)' },
  };
  const s = {
    width: d, height: d, borderRadius: '50%', border: 'none',
    display: 'grid', placeItems: 'center', cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.4 : 1, WebkitTapHighlightColor: 'transparent',
    transition: 'background var(--dur-fast) var(--ease-out), transform var(--dur-fast) var(--ease-out)',
    ...(tones[tone] || tones.default), ...style,
  };
  return (
    <button
      type="button" style={s} onClick={onClick} disabled={disabled} aria-label={ariaLabel || icon}
      onMouseDown={(e) => !disabled && (e.currentTarget.style.transform = 'scale(0.9)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      {...rest}
    >
      <i data-lucide={icon} style={{ width: d * 0.45, height: d * 0.45 }} />
    </button>
  );
}
