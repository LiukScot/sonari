import React from 'react';

/**
 * PlayButton — the master play/pause. Large, gradient-filled, glowing.
 * This is the one element that always wears the full twilight gradient.
 */
export function PlayButton({ playing = false, size = 64, onClick, style = {} }) {
  const s = {
    width: size, height: size, borderRadius: '50%', border: 'none',
    background: 'var(--accent-gradient)', color: 'var(--text-on-accent)',
    display: 'grid', placeItems: 'center', cursor: 'pointer',
    boxShadow: 'var(--glow-accent)', WebkitTapHighlightColor: 'transparent',
    transition: 'transform var(--dur-fast) var(--ease-soft), filter var(--dur-fast) var(--ease-out)',
    ...style,
  };
  return (
    <button
      type="button" style={s} onClick={onClick}
      onMouseDown={(e) => (e.currentTarget.style.transform = 'scale(0.93)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      aria-label={playing ? 'Pause' : 'Play'}
    >
      <i data-lucide={playing ? 'pause' : 'play'}
         style={{ width: size * 0.4, height: size * 0.4, marginLeft: playing ? 0 : size * 0.04 }} />
    </button>
  );
}
