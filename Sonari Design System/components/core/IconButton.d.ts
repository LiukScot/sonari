import React from 'react';

export interface IconButtonProps {
  /** Lucide icon name. */
  icon?: string;
  size?: 'sm' | 'md' | 'lg';
  /** `default` (transparent) or `raised` (dark surface). */
  tone?: 'default' | 'raised';
  active?: boolean;
  disabled?: boolean;
  onClick?: () => void;
  'aria-label'?: string;
  style?: React.CSSProperties;
}

/** Round, quiet icon control for top bars and the playback bar. */
export function IconButton(props: IconButtonProps): JSX.Element;
