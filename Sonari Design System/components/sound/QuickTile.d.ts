import React from 'react';

export interface QuickTileProps {
  /** Tile title (preset or "Sonari"). */
  label?: string;
  /** Secondary line; defaults to Playing / Tap to start. */
  sub?: string;
  /** Lucide icon name. */
  icon?: string;
  /** Active = filled with the twilight gradient (Android accent-fill). */
  active?: boolean;
  onClick?: () => void;
  style?: React.CSSProperties;
}

/** Sonari's signature differentiator — an Android Quick Settings tile. */
export function QuickTile(props: QuickTileProps): JSX.Element;
