import React from 'react';

export interface SwitchProps {
  checked?: boolean;
  disabled?: boolean;
  onChange?: (next: boolean) => void;
  style?: React.CSSProperties;
}

/** On/off toggle; the track fills with the twilight gradient when on. */
export function Switch(props: SwitchProps): JSX.Element;
