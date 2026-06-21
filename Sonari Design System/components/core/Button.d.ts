import React from 'react';

export interface ButtonProps {
  /** Visual style. primary = twilight gradient (one per view). */
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  /** Lucide icon name shown before the label. */
  icon?: string;
  /** Lucide icon name shown after the label. */
  iconRight?: string;
  /** Stretch to container width. */
  full?: boolean;
  disabled?: boolean;
  children?: React.ReactNode;
  style?: React.CSSProperties;
  onClick?: (e: React.MouseEvent) => void;
}

/** Sonari's primary action button. */
export function Button(props: ButtonProps): JSX.Element;
