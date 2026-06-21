import React from 'react';

export interface PlayButtonProps {
  /** Whether audio is currently playing (shows pause icon). */
  playing?: boolean;
  /** Diameter in px. Default 64. */
  size?: number;
  onClick?: () => void;
  style?: React.CSSProperties;
}

/** The master play/pause control — always wears the full twilight gradient. */
export function PlayButton(props: PlayButtonProps): JSX.Element;
