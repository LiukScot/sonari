import React from 'react';

export interface BadgeProps {
  children?: React.ReactNode;
  /** neutral | accent | gradient | success | outline */
  variant?: 'neutral' | 'accent' | 'gradient' | 'success' | 'outline';
  /** Optional leading Lucide icon. */
  icon?: string;
  style?: React.CSSProperties;
}

/** Small status / category pill (preset labels, "Active", counts). */
export function Badge(props: BadgeProps): JSX.Element;
