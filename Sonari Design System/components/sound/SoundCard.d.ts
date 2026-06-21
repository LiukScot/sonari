import React from 'react';

export interface SoundCardProps {
  /** Sound label, e.g. "Rain". */
  name?: string;
  /** Lucide icon name for the sound. */
  icon?: string;
  /** Current volume 0–100. Drives the active/lit state when > 0. */
  volume?: number;
  /** Force active state (defaults to volume > 0). */
  active?: boolean;
  /** Tap on the card body (toggle on/off). */
  onToggle?: () => void;
  /** Drag/click on the slider — receives new volume 0–100. */
  onVolume?: (v: number) => void;
  style?: React.CSSProperties;
}

/** Sonari's signature sound tile. */
export function SoundCard(props: SoundCardProps): JSX.Element;
