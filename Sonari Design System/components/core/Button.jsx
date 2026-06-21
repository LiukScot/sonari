import React from 'react';

/**
 * Sonari Button — the brand's primary action.
 * `primary` wears the twilight gradient (use sparingly, one per view).
 * `secondary` is a raised dark surface; `ghost` is text-only.
 */
export function Button({
  variant = 'primary',
  size = 'md',
  icon,
  iconRight,
  full = false,
  disabled = false,
  children,
  style = {},
  ...rest
}) {
  const sizes = {
    sm: { h: 36, px: 14, fs: 'var(--t-caption)', gap: 6 },
    md: { h: 44, px: 18, fs: 'var(--t-label)',   gap: 8 },
    lg: { h: 52, px: 24, fs: 'var(--t-body)',    gap: 10 },
  };
  const s = sizes[size] || sizes.md;

  const base = {
    display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
    gap: s.gap, height: s.h, padding: `0 ${s.px}px`, width: full ? '100%' : 'auto',
    fontFamily: 'var(--font-sans)', fontSize: s.fs, fontWeight: 'var(--w-semibold)',
    letterSpacing: '0.01em', borderRadius: 'var(--r-pill)', border: '1px solid transparent',
    cursor: disabled ? 'not-allowed' : 'pointer', opacity: disabled ? 0.45 : 1,
    transition: 'transform var(--dur-fast) var(--ease-out), filter var(--dur-fast) var(--ease-out), background var(--dur-fast) var(--ease-out)',
    whiteSpace: 'nowrap', userSelect: 'none', WebkitTapHighlightColor: 'transparent',
  };

  const variants = {
    primary: {
      background: 'var(--accent-gradient)', color: 'var(--text-on-accent)',
      boxShadow: 'var(--glow-accent)', fontWeight: 'var(--w-bold)',
    },
    secondary: {
      background: 'var(--surface-raised)', color: 'var(--text-strong)',
      borderColor: 'var(--border-default)',
    },
    ghost: {
      background: 'transparent', color: 'var(--accent-solid)',
    },
    danger: {
      background: 'transparent', color: 'var(--danger)',
      borderColor: 'rgba(255,139,139,0.4)',
    },
  };

  return (
    <button
      type="button"
      disabled={disabled}
      style={{ ...base, ...(variants[variant] || variants.primary), ...style }}
      onMouseDown={(e) => !disabled && (e.currentTarget.style.transform = 'scale(0.97)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      {...rest}
    >
      {icon && <i data-lucide={icon} style={{ width: 18, height: 18 }} />}
      {children}
      {iconRight && <i data-lucide={iconRight} style={{ width: 18, height: 18 }} />}
    </button>
  );
}
