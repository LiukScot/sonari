import React from 'react';

export interface SegmentOption {
  value: string;
  label?: string;
  /** Optional Lucide icon name. */
  icon?: string;
}

export interface SegmentedControlProps {
  /** Options as strings or {value,label,icon}. */
  options?: (string | SegmentOption)[];
  value?: string;
  onChange?: (value: string) => void;
  style?: React.CSSProperties;
}

/** Pill segmented control for exclusive choices (theme light/dark/system, tabs). */
export function SegmentedControl(props: SegmentedControlProps): JSX.Element;
