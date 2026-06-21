import React from 'react';

export interface VolumeSliderProps {
  /** 0–100. */
  value?: number;
  /** Optional leading Lucide icon (e.g. "volume-2"). */
  icon?: string;
  /** Gradient fill when true, muted grey when false. */
  active?: boolean;
  /** Show the numeric value on the right. */
  label?: boolean;
  onChange?: (v: number) => void;
  style?: React.CSSProperties;
}

/** Horizontal volume slider with the twilight-gradient fill (master & per-sound). */
export function VolumeSlider(props: VolumeSliderProps): JSX.Element;
