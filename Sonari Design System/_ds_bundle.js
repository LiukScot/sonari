/* @ds-bundle: {"format":3,"namespace":"SonariDesignSystem_901849","components":[{"name":"Badge","sourcePath":"components/core/Badge.jsx"},{"name":"Button","sourcePath":"components/core/Button.jsx"},{"name":"IconButton","sourcePath":"components/core/IconButton.jsx"},{"name":"PlayButton","sourcePath":"components/core/PlayButton.jsx"},{"name":"SegmentedControl","sourcePath":"components/core/SegmentedControl.jsx"},{"name":"Switch","sourcePath":"components/core/Switch.jsx"},{"name":"VolumeSlider","sourcePath":"components/core/VolumeSlider.jsx"},{"name":"QuickTile","sourcePath":"components/sound/QuickTile.jsx"},{"name":"SoundCard","sourcePath":"components/sound/SoundCard.jsx"}],"sourceHashes":{"components/core/Badge.jsx":"967f630c9cca","components/core/Button.jsx":"864d9d981325","components/core/IconButton.jsx":"271e7bcbded8","components/core/PlayButton.jsx":"8e6e33a6b6ff","components/core/SegmentedControl.jsx":"09b07ba053e1","components/core/Switch.jsx":"38944dc75042","components/core/VolumeSlider.jsx":"931b338888f0","components/sound/QuickTile.jsx":"aea5500404a1","components/sound/SoundCard.jsx":"108542aa8b72","ui_kits/sonari-app/MixerScreen.jsx":"2b7abc925b73","ui_kits/sonari-app/PresetsScreen.jsx":"51dc39113ef0","ui_kits/sonari-app/SettingsScreen.jsx":"61c2144106ef","ui_kits/sonari-app/ShadeScreen.jsx":"359643907abb","ui_kits/sonari-app/data.js":"d041c3f06e4d"},"inlinedExternals":[],"unexposedExports":[]} */

(() => {

const __ds_ns = (window.SonariDesignSystem_901849 = window.SonariDesignSystem_901849 || {});

const __ds_scope = {};

(__ds_ns.__errors = __ds_ns.__errors || []);

// components/core/Badge.jsx
try { (() => {
/** Badge / Tag — small status or category pill. */
function Badge({
  children,
  variant = 'neutral',
  icon,
  style = {}
}) {
  const variants = {
    neutral: {
      background: 'var(--surface-raised)',
      color: 'var(--text-muted)',
      border: 'var(--border-default)'
    },
    accent: {
      background: 'var(--accent-016)',
      color: 'var(--accent-solid)',
      border: 'transparent'
    },
    gradient: {
      background: 'var(--accent-gradient)',
      color: 'var(--text-on-accent)',
      border: 'transparent'
    },
    success: {
      background: 'rgba(127,214,166,0.16)',
      color: 'var(--success)',
      border: 'transparent'
    },
    outline: {
      background: 'transparent',
      color: 'var(--text-body)',
      border: 'var(--border-default)'
    }
  };
  const v = variants[variant] || variants.neutral;
  return /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 5,
      height: 24,
      padding: '0 10px',
      borderRadius: 'var(--r-pill)',
      fontFamily: 'var(--font-sans)',
      fontSize: 'var(--t-micro)',
      fontWeight: 600,
      letterSpacing: '0.02em',
      background: v.background,
      color: v.color,
      border: `1px solid ${v.border}`,
      whiteSpace: 'nowrap',
      ...style
    }
  }, icon && /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 13,
      height: 13
    }
  }), children);
}
Object.assign(__ds_scope, { Badge });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Badge.jsx", error: String((e && e.message) || e) }); }

// components/core/Button.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
/**
 * Sonari Button — the brand's primary action.
 * `primary` wears the twilight gradient (use sparingly, one per view).
 * `secondary` is a raised dark surface; `ghost` is text-only.
 */
function Button({
  variant = 'primary',
  size = 'md',
  icon,
  iconRight,
  full = false,
  disabled = false,
  children,
  style = {},
  ...rest
}) {
  const sizes = {
    sm: {
      h: 36,
      px: 14,
      fs: 'var(--t-caption)',
      gap: 6
    },
    md: {
      h: 44,
      px: 18,
      fs: 'var(--t-label)',
      gap: 8
    },
    lg: {
      h: 52,
      px: 24,
      fs: 'var(--t-body)',
      gap: 10
    }
  };
  const s = sizes[size] || sizes.md;
  const base = {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: s.gap,
    height: s.h,
    padding: `0 ${s.px}px`,
    width: full ? '100%' : 'auto',
    fontFamily: 'var(--font-sans)',
    fontSize: s.fs,
    fontWeight: 'var(--w-semibold)',
    letterSpacing: '0.01em',
    borderRadius: 'var(--r-pill)',
    border: '1px solid transparent',
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.45 : 1,
    transition: 'transform var(--dur-fast) var(--ease-out), filter var(--dur-fast) var(--ease-out), background var(--dur-fast) var(--ease-out)',
    whiteSpace: 'nowrap',
    userSelect: 'none',
    WebkitTapHighlightColor: 'transparent'
  };
  const variants = {
    primary: {
      background: 'var(--accent-gradient)',
      color: 'var(--text-on-accent)',
      boxShadow: 'var(--glow-accent)',
      fontWeight: 'var(--w-bold)'
    },
    secondary: {
      background: 'var(--surface-raised)',
      color: 'var(--text-strong)',
      borderColor: 'var(--border-default)'
    },
    ghost: {
      background: 'transparent',
      color: 'var(--accent-solid)'
    },
    danger: {
      background: 'transparent',
      color: 'var(--danger)',
      borderColor: 'rgba(255,139,139,0.4)'
    }
  };
  return /*#__PURE__*/React.createElement("button", _extends({
    type: "button",
    disabled: disabled,
    style: {
      ...base,
      ...(variants[variant] || variants.primary),
      ...style
    },
    onMouseDown: e => !disabled && (e.currentTarget.style.transform = 'scale(0.97)'),
    onMouseUp: e => e.currentTarget.style.transform = 'scale(1)',
    onMouseLeave: e => e.currentTarget.style.transform = 'scale(1)'
  }, rest), icon && /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 18,
      height: 18
    }
  }), children, iconRight && /*#__PURE__*/React.createElement("i", {
    "data-lucide": iconRight,
    style: {
      width: 18,
      height: 18
    }
  }));
}
Object.assign(__ds_scope, { Button });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Button.jsx", error: String((e && e.message) || e) }); }

// components/core/IconButton.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
/** IconButton — round, quiet icon control for toolbars and bars. */
function IconButton({
  icon = 'menu',
  size = 'md',
  tone = 'default',
  active = false,
  disabled = false,
  onClick,
  style = {},
  'aria-label': ariaLabel,
  ...rest
}) {
  const sizes = {
    sm: 36,
    md: 44,
    lg: 52
  };
  const d = sizes[size] || 44;
  const tones = {
    default: {
      color: active ? 'var(--accent-solid)' : 'var(--text-muted)',
      background: active ? 'var(--accent-016)' : 'transparent'
    },
    raised: {
      color: 'var(--text-strong)',
      background: 'var(--surface-raised)'
    }
  };
  const s = {
    width: d,
    height: d,
    borderRadius: '50%',
    border: 'none',
    display: 'grid',
    placeItems: 'center',
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.4 : 1,
    WebkitTapHighlightColor: 'transparent',
    transition: 'background var(--dur-fast) var(--ease-out), transform var(--dur-fast) var(--ease-out)',
    ...(tones[tone] || tones.default),
    ...style
  };
  return /*#__PURE__*/React.createElement("button", _extends({
    type: "button",
    style: s,
    onClick: onClick,
    disabled: disabled,
    "aria-label": ariaLabel || icon,
    onMouseDown: e => !disabled && (e.currentTarget.style.transform = 'scale(0.9)'),
    onMouseUp: e => e.currentTarget.style.transform = 'scale(1)',
    onMouseLeave: e => e.currentTarget.style.transform = 'scale(1)'
  }, rest), /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: d * 0.45,
      height: d * 0.45
    }
  }));
}
Object.assign(__ds_scope, { IconButton });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/IconButton.jsx", error: String((e && e.message) || e) }); }

// components/core/PlayButton.jsx
try { (() => {
/**
 * PlayButton — the master play/pause. Large, gradient-filled, glowing.
 * This is the one element that always wears the full twilight gradient.
 */
function PlayButton({
  playing = false,
  size = 64,
  onClick,
  style = {}
}) {
  const s = {
    width: size,
    height: size,
    borderRadius: '50%',
    border: 'none',
    background: 'var(--accent-gradient)',
    color: 'var(--text-on-accent)',
    display: 'grid',
    placeItems: 'center',
    cursor: 'pointer',
    boxShadow: 'var(--glow-accent)',
    WebkitTapHighlightColor: 'transparent',
    transition: 'transform var(--dur-fast) var(--ease-soft), filter var(--dur-fast) var(--ease-out)',
    ...style
  };
  return /*#__PURE__*/React.createElement("button", {
    type: "button",
    style: s,
    onClick: onClick,
    onMouseDown: e => e.currentTarget.style.transform = 'scale(0.93)',
    onMouseUp: e => e.currentTarget.style.transform = 'scale(1)',
    onMouseLeave: e => e.currentTarget.style.transform = 'scale(1)',
    "aria-label": playing ? 'Pause' : 'Play'
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": playing ? 'pause' : 'play',
    style: {
      width: size * 0.4,
      height: size * 0.4,
      marginLeft: playing ? 0 : size * 0.04
    }
  }));
}
Object.assign(__ds_scope, { PlayButton });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/PlayButton.jsx", error: String((e && e.message) || e) }); }

// components/core/SegmentedControl.jsx
try { (() => {
/** SegmentedControl — pill row for exclusive choices (e.g. theme, tabs). */
function SegmentedControl({
  options = [],
  value,
  onChange,
  style = {}
}) {
  const norm = options.map(o => typeof o === 'string' ? {
    value: o,
    label: o
  } : o);
  const current = value != null ? value : norm[0] && norm[0].value;
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'inline-flex',
      padding: 4,
      gap: 2,
      borderRadius: 'var(--r-pill)',
      background: 'var(--surface-raised)',
      border: '1px solid var(--border-default)',
      ...style
    }
  }, norm.map(o => {
    const sel = o.value === current;
    return /*#__PURE__*/React.createElement("button", {
      key: o.value,
      type: "button",
      onClick: () => onChange && onChange(o.value),
      style: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: 6,
        height: 34,
        padding: '0 16px',
        border: 'none',
        cursor: 'pointer',
        borderRadius: 'var(--r-pill)',
        fontFamily: 'var(--font-sans)',
        fontSize: 'var(--t-caption)',
        fontWeight: 600,
        color: sel ? 'var(--text-on-accent)' : 'var(--text-muted)',
        background: sel ? 'var(--accent-gradient)' : 'transparent',
        boxShadow: sel ? 'var(--glow-accent)' : 'none',
        transition: 'all var(--dur-fast) var(--ease-out)',
        WebkitTapHighlightColor: 'transparent'
      }
    }, o.icon && /*#__PURE__*/React.createElement("i", {
      "data-lucide": o.icon,
      style: {
        width: 15,
        height: 15
      }
    }), o.label);
  }));
}
Object.assign(__ds_scope, { SegmentedControl });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/SegmentedControl.jsx", error: String((e && e.message) || e) }); }

// components/core/Switch.jsx
try { (() => {
/** Switch — on/off toggle. Twilight gradient track when on. */
function Switch({
  checked = false,
  disabled = false,
  onChange,
  style = {}
}) {
  const track = {
    width: 46,
    height: 28,
    borderRadius: 'var(--r-pill)',
    padding: 3,
    background: checked ? 'var(--accent-gradient)' : 'var(--surface-pressed)',
    border: `1px solid ${checked ? 'transparent' : 'var(--border-default)'}`,
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.45 : 1,
    transition: 'background var(--dur-base) var(--ease-out)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: checked ? 'flex-end' : 'flex-start',
    WebkitTapHighlightColor: 'transparent',
    ...style
  };
  const knob = {
    width: 20,
    height: 20,
    borderRadius: '50%',
    background: '#fff',
    boxShadow: '0 1px 3px rgba(0,0,0,0.4)',
    transition: 'all var(--dur-base) var(--ease-soft)'
  };
  return /*#__PURE__*/React.createElement("button", {
    type: "button",
    role: "switch",
    "aria-checked": checked,
    disabled: disabled,
    style: track,
    onClick: () => !disabled && onChange && onChange(!checked)
  }, /*#__PURE__*/React.createElement("div", {
    style: knob
  }));
}
Object.assign(__ds_scope, { Switch });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Switch.jsx", error: String((e && e.message) || e) }); }

// components/core/VolumeSlider.jsx
try { (() => {
/** VolumeSlider — horizontal slider with twilight-gradient fill. */
function VolumeSlider({
  value = 50,
  icon,
  active = true,
  onChange,
  label,
  style = {}
}) {
  const track = {
    position: 'relative',
    flex: 1,
    height: 8,
    borderRadius: 'var(--r-pill)',
    background: 'var(--surface-pressed)',
    cursor: 'pointer'
  };
  const fill = {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: `${value}%`,
    borderRadius: 'var(--r-pill)',
    background: active ? 'var(--accent-gradient)' : 'var(--text-faint)',
    transition: 'width var(--dur-fast) var(--ease-out)'
  };
  const knob = {
    position: 'absolute',
    top: '50%',
    left: `${value}%`,
    transform: 'translate(-50%, -50%)',
    width: 18,
    height: 18,
    borderRadius: '50%',
    background: '#fff',
    boxShadow: '0 2px 6px rgba(0,0,0,0.5)',
    pointerEvents: 'none'
  };
  const set = e => {
    if (!onChange) return;
    const r = e.currentTarget.getBoundingClientRect();
    onChange(Math.round(Math.max(0, Math.min(100, (e.clientX - r.left) / r.width * 100))));
  };
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--sp-3)',
      ...style
    }
  }, icon && /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 20,
      height: 20,
      color: 'var(--text-muted)',
      flex: '0 0 auto'
    }
  }), /*#__PURE__*/React.createElement("div", {
    style: track,
    onClick: set
  }, /*#__PURE__*/React.createElement("div", {
    style: fill
  }), /*#__PURE__*/React.createElement("div", {
    style: knob
  })), label && /*#__PURE__*/React.createElement("span", {
    style: {
      fontFamily: 'var(--font-mono)',
      fontSize: 'var(--t-caption)',
      color: 'var(--text-muted)',
      width: 34,
      textAlign: 'right'
    }
  }, value));
}
Object.assign(__ds_scope, { VolumeSlider });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/VolumeSlider.jsx", error: String((e && e.message) || e) }); }

// components/sound/QuickTile.jsx
try { (() => {
/**
 * QuickTile — a recreation of an Android Quick Settings tile, Sonari's
 * signature differentiator. Active tiles fill with the twilight gradient
 * (Android's accent-fill convention); inactive tiles are an outlined chip.
 */
function QuickTile({
  label = 'Sonari',
  sub,
  icon = 'audio-lines',
  active = false,
  onClick,
  style = {}
}) {
  const wrap = {
    display: 'flex',
    alignItems: 'center',
    gap: 'var(--sp-3)',
    minWidth: 150,
    height: 56,
    padding: '0 16px',
    borderRadius: 'var(--r-xl)',
    cursor: 'pointer',
    WebkitTapHighlightColor: 'transparent',
    background: active ? 'var(--accent-gradient)' : 'var(--surface-raised)',
    border: `1px solid ${active ? 'transparent' : 'var(--border-hover)'}`,
    boxShadow: active ? 'var(--glow-accent)' : 'none',
    transition: 'all var(--dur-base) var(--ease-out)',
    ...style
  };
  const ic = {
    width: 38,
    height: 38,
    borderRadius: '50%',
    display: 'grid',
    placeItems: 'center',
    flex: '0 0 auto',
    background: active ? 'rgba(26,20,48,0.18)' : 'var(--surface-pressed)',
    color: active ? 'var(--text-on-accent)' : 'var(--text-muted)'
  };
  return /*#__PURE__*/React.createElement("div", {
    style: wrap,
    onClick: onClick
  }, /*#__PURE__*/React.createElement("div", {
    style: ic
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 20,
      height: 20
    }
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      fontFamily: 'var(--font-sans)',
      fontSize: 'var(--t-label)',
      fontWeight: 700,
      color: active ? 'var(--text-on-accent)' : 'var(--text-strong)',
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis'
    }
  }, label), /*#__PURE__*/React.createElement("span", {
    style: {
      fontFamily: 'var(--font-sans)',
      fontSize: 'var(--t-micro)',
      fontWeight: 500,
      color: active ? 'rgba(26,20,48,0.7)' : 'var(--text-faint)',
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis'
    }
  }, sub || (active ? 'Playing' : 'Tap to start'))));
}
Object.assign(__ds_scope, { QuickTile });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/sound/QuickTile.jsx", error: String((e && e.message) || e) }); }

// components/sound/SoundCard.jsx
try { (() => {
/**
 * SoundCard — Sonari's signature tile. Icon + name + volume slider.
 * When active (volume > 0) it lights up with the twilight gradient ring
 * and a soft glow, matching PLAN §5 ("active card highlighted").
 */
function SoundCard({
  name = 'Rain',
  icon = 'cloud-rain',
  volume = 0,
  // 0..100
  active,
  // optional override; defaults to volume > 0
  onToggle,
  onVolume,
  style = {}
}) {
  const isActive = active != null ? active : volume > 0;
  const wrap = {
    position: 'relative',
    display: 'flex',
    flexDirection: 'column',
    gap: 'var(--sp-3)',
    padding: 'var(--card-pad)',
    borderRadius: 'var(--r-md)',
    background: isActive ? 'var(--surface-card-grad)' : 'var(--surface-card)',
    border: `1px solid ${isActive ? 'transparent' : 'var(--border-default)'}`,
    boxShadow: isActive ? 'var(--glow-accent)' : 'var(--shadow-sm)',
    cursor: 'pointer',
    userSelect: 'none',
    WebkitTapHighlightColor: 'transparent',
    transition: 'background var(--dur-base) var(--ease-out), box-shadow var(--dur-base) var(--ease-out), transform var(--dur-fast) var(--ease-out)',
    overflow: 'hidden',
    ...style
  };
  // gradient hairline ring when active
  const ring = isActive ? {
    position: 'absolute',
    inset: 0,
    borderRadius: 'var(--r-md)',
    padding: 1,
    background: 'var(--accent-gradient)',
    WebkitMask: 'linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0)',
    WebkitMaskComposite: 'xor',
    maskComposite: 'exclude',
    pointerEvents: 'none'
  } : {
    display: 'none'
  };
  const iconWrap = {
    width: 42,
    height: 42,
    borderRadius: 'var(--r-sm)',
    display: 'grid',
    placeItems: 'center',
    background: isActive ? 'var(--accent-016)' : 'var(--surface-raised)',
    color: isActive ? 'var(--accent-solid)' : 'var(--text-muted)',
    transition: 'all var(--dur-base) var(--ease-out)'
  };
  return /*#__PURE__*/React.createElement("div", {
    style: wrap,
    onClick: () => onToggle && onToggle(),
    onMouseDown: e => e.currentTarget.style.transform = 'scale(0.98)',
    onMouseUp: e => e.currentTarget.style.transform = 'scale(1)',
    onMouseLeave: e => e.currentTarget.style.transform = 'scale(1)'
  }, /*#__PURE__*/React.createElement("div", {
    style: ring
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: iconWrap
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 22,
      height: 22
    }
  })), /*#__PURE__*/React.createElement("span", {
    style: {
      fontFamily: 'var(--font-mono)',
      fontSize: 'var(--t-micro)',
      fontWeight: 600,
      color: isActive ? 'var(--accent-solid)' : 'var(--text-faint)',
      letterSpacing: '0.02em'
    }
  }, isActive ? `${volume}` : 'OFF')), /*#__PURE__*/React.createElement("div", {
    style: {
      fontFamily: 'var(--font-sans)',
      fontSize: 'var(--t-label)',
      fontWeight: 600,
      color: isActive ? 'var(--text-strong)' : 'var(--text-body)'
    }
  }, name), /*#__PURE__*/React.createElement(Slider, {
    value: volume,
    active: isActive,
    onChange: onVolume
  }));
}
function Slider({
  value = 0,
  active,
  onChange
}) {
  const track = {
    position: 'relative',
    height: 6,
    borderRadius: 'var(--r-pill)',
    background: 'var(--surface-pressed)',
    cursor: 'pointer'
  };
  const fill = {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    width: `${value}%`,
    borderRadius: 'var(--r-pill)',
    background: active ? 'var(--accent-gradient)' : 'var(--text-faint)',
    transition: 'width var(--dur-fast) var(--ease-out)'
  };
  const knob = {
    position: 'absolute',
    top: '50%',
    left: `${value}%`,
    transform: 'translate(-50%, -50%)',
    width: 14,
    height: 14,
    borderRadius: '50%',
    background: '#fff',
    boxShadow: '0 1px 4px rgba(0,0,0,0.5)',
    pointerEvents: 'none',
    opacity: active ? 1 : 0.4
  };
  return /*#__PURE__*/React.createElement("div", {
    style: track,
    onClick: e => {
      if (!onChange) return;
      const r = e.currentTarget.getBoundingClientRect();
      onChange(Math.round(Math.max(0, Math.min(100, (e.clientX - r.left) / r.width * 100))));
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: fill
  }), /*#__PURE__*/React.createElement("div", {
    style: knob
  }));
}
Object.assign(__ds_scope, { SoundCard });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/sound/SoundCard.jsx", error: String((e && e.message) || e) }); }

// ui_kits/sonari-app/MixerScreen.jsx
try { (() => {
// MixerScreen — Sonari home: preset chips, sound grid, playback bar.
const DS = window.SonariDesignSystem_901849;
function PresetChip({
  preset,
  active,
  onClick
}) {
  return /*#__PURE__*/React.createElement("button", {
    onClick: onClick,
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 8,
      flex: '0 0 auto',
      height: 38,
      padding: '0 14px 0 12px',
      borderRadius: 'var(--r-pill)',
      cursor: 'pointer',
      border: `1px solid ${active ? 'transparent' : 'var(--border-default)'}`,
      background: active ? 'var(--accent-016)' : 'var(--surface-card)',
      color: active ? 'var(--accent-solid)' : 'var(--text-muted)',
      fontFamily: 'var(--font-sans)',
      fontSize: 'var(--t-caption)',
      fontWeight: 600,
      WebkitTapHighlightColor: 'transparent'
    }
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": preset.icon,
    style: {
      width: 15,
      height: 15
    }
  }), preset.name, preset.tile && /*#__PURE__*/React.createElement("i", {
    "data-lucide": "zap",
    style: {
      width: 12,
      height: 12,
      opacity: 0.8
    }
  }));
}
function MixerScreen({
  sounds,
  mix,
  setVol,
  playing,
  onPlayToggle,
  master,
  setMaster,
  presets,
  activePreset,
  onPreset,
  onOpenTimer,
  onOpenSettings,
  timerLabel
}) {
  const activeCount = Object.values(mix).filter(v => v > 0).length;
  return /*#__PURE__*/React.createElement("div", {
    style: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      background: 'var(--surface-app)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 11,
      padding: '18px 18px 14px'
    }
  }, /*#__PURE__*/React.createElement("img", {
    src: "../../assets/sonari-mark.svg",
    width: "30",
    height: "30",
    alt: ""
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-title)',
      fontWeight: 800,
      color: 'var(--text-strong)',
      letterSpacing: '-0.02em'
    }
  }, "Sonari")), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      overflowY: 'auto',
      padding: '4px 18px 18px'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'repeat(3, 1fr)',
      gap: 'var(--grid-gap)'
    }
  }, sounds.map(s => /*#__PURE__*/React.createElement(DS.SoundCard, {
    key: s.id,
    name: s.name,
    icon: s.icon,
    volume: mix[s.id] || 0,
    onToggle: () => setVol(s.id, (mix[s.id] || 0) > 0 ? 0 : 55),
    onVolume: v => setVol(s.id, v)
  })))), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: '0 0 auto',
      padding: '12px 18px calc(12px + env(safe-area-inset-bottom))',
      background: 'var(--ink-1)',
      borderTop: '1px solid var(--line-1)',
      borderTopLeftRadius: 'var(--r-xl)',
      borderTopRightRadius: 'var(--r-xl)',
      boxShadow: '0 -8px 30px rgba(0,0,0,0.4)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 16
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: 8
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      fontSize: 'var(--t-caption)',
      fontWeight: 600,
      color: 'var(--text-body)'
    }
  }, activeCount > 0 ? `${activeCount} sound${activeCount > 1 ? 's' : ''} mixing` : 'No sounds yet'), timerLabel && /*#__PURE__*/React.createElement("span", {
    style: {
      fontFamily: 'var(--font-mono)',
      fontSize: 11,
      color: 'var(--accent-solid)'
    }
  }, timerLabel)), /*#__PURE__*/React.createElement(DS.VolumeSlider, {
    value: master,
    icon: "volume-2",
    active: playing && activeCount > 0,
    onChange: setMaster
  })), /*#__PURE__*/React.createElement(DS.IconButton, {
    icon: timerLabel ? 'timer' : 'moon',
    tone: "raised",
    active: !!timerLabel,
    onClick: onOpenTimer,
    "aria-label": "Sleep timer"
  }), /*#__PURE__*/React.createElement(DS.PlayButton, {
    playing: playing,
    size: 62,
    onClick: onPlayToggle
  }))));
}
Object.assign(window, {
  MixerScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/sonari-app/MixerScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/sonari-app/PresetsScreen.jsx
try { (() => {
// PresetsScreen — saved mixes, each assignable to a Quick Settings tile.
const DSp = window.SonariDesignSystem_901849;
function PresetRow({
  preset,
  sounds,
  onPlay,
  onToggleTile,
  playingId
}) {
  const ids = Object.keys(preset.mix);
  const isPlaying = playingId === preset.id;
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 12,
      padding: 14,
      borderRadius: 'var(--r-md)',
      background: isPlaying ? 'var(--surface-card-grad)' : 'var(--surface-card)',
      border: `1px solid ${isPlaying ? 'transparent' : 'var(--border-default)'}`,
      boxShadow: isPlaying ? 'var(--glow-accent)' : 'var(--shadow-sm)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 12
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: 44,
      height: 44,
      borderRadius: 'var(--r-sm)',
      display: 'grid',
      placeItems: 'center',
      background: 'var(--accent-016)',
      color: 'var(--accent-solid)',
      flex: '0 0 auto'
    }
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": preset.icon,
    style: {
      width: 22,
      height: 22
    }
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-label)',
      fontWeight: 700,
      color: 'var(--text-strong)'
    }
  }, preset.name), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 8,
      marginTop: 5,
      color: 'var(--text-faint)'
    }
  }, ids.map(id => {
    const s = sounds.find(x => x.id === id);
    return s ? /*#__PURE__*/React.createElement("i", {
      key: id,
      "data-lucide": s.icon,
      style: {
        width: 15,
        height: 15
      }
    }) : null;
  }))), /*#__PURE__*/React.createElement(DSp.IconButton, {
    icon: isPlaying ? 'pause' : 'play',
    tone: "raised",
    onClick: () => onPlay(preset.id)
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingTop: 10,
      borderTop: '1px solid var(--line-1)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 7,
      fontSize: 'var(--t-caption)',
      color: 'var(--text-muted)',
      fontWeight: 600
    }
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "zap",
    style: {
      width: 14,
      height: 14,
      color: preset.tile ? 'var(--accent-solid)' : 'var(--text-faint)'
    }
  }), "Quick Settings tile"), /*#__PURE__*/React.createElement(DSp.Switch, {
    checked: preset.tile,
    onChange: () => onToggleTile(preset.id)
  })));
}
function PresetsScreen({
  presets,
  sounds,
  onPlay,
  onToggleTile,
  playingId
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      background: 'var(--surface-app)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      padding: '16px 18px 8px'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-title)',
      fontWeight: 800,
      color: 'var(--text-strong)',
      letterSpacing: '-0.02em'
    }
  }, "Presets"), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-caption)',
      color: 'var(--text-muted)',
      marginTop: 3
    }
  }, "Save a mix, then start it from the shade.")), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      overflowY: 'auto',
      padding: '8px 18px 18px',
      display: 'flex',
      flexDirection: 'column',
      gap: 12
    }
  }, presets.map(p => /*#__PURE__*/React.createElement(PresetRow, {
    key: p.id,
    preset: p,
    sounds: sounds,
    onPlay: onPlay,
    onToggleTile: onToggleTile,
    playingId: playingId
  })), /*#__PURE__*/React.createElement(DSp.Button, {
    variant: "secondary",
    icon: "plus",
    full: true
  }, "New preset from current mix")));
}
Object.assign(window, {
  PresetsScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/sonari-app/PresetsScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/sonari-app/SettingsScreen.jsx
try { (() => {
// SettingsScreen — audio behaviour and the configurable Quick Tile.
const DSs = window.SonariDesignSystem_901849;
function Row({
  icon,
  title,
  sub,
  children,
  last
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 12,
      padding: '14px 0',
      borderBottom: last ? 'none' : '1px solid var(--line-1)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: 38,
      height: 38,
      borderRadius: 'var(--r-sm)',
      flex: '0 0 auto',
      display: 'grid',
      placeItems: 'center',
      background: 'var(--surface-raised)',
      color: 'var(--accent-solid)'
    }
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": icon,
    style: {
      width: 19,
      height: 19
    }
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-label)',
      fontWeight: 600,
      color: 'var(--text-strong)'
    }
  }, title), sub && /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 12,
      color: 'var(--text-muted)',
      marginTop: 2
    }
  }, sub)), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: '0 0 auto'
    }
  }, children));
}
function Section({
  title,
  children
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      marginBottom: 18
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-micro)',
      letterSpacing: 'var(--track-caps)',
      textTransform: 'uppercase',
      color: 'var(--text-faint)',
      fontWeight: 700,
      margin: '0 0 6px 2px'
    }
  }, title), /*#__PURE__*/React.createElement("div", {
    style: {
      padding: '0 14px',
      borderRadius: 'var(--r-md)',
      background: 'var(--surface-card)',
      border: '1px solid var(--border-default)'
    }
  }, children));
}
function SettingsScreen({
  fade,
  setFade,
  focusDuck,
  setFocusDuck
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      background: 'var(--surface-app)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      padding: '16px 18px 8px'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-title)',
      fontWeight: 800,
      color: 'var(--text-strong)',
      letterSpacing: '-0.02em'
    }
  }, "Settings")), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      overflowY: 'auto',
      padding: '10px 18px 18px'
    }
  }, /*#__PURE__*/React.createElement(Section, {
    title: "Playback"
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "audio-lines",
    title: "Fade in / out",
    sub: "~1s when you play or pause"
  }, /*#__PURE__*/React.createElement(DSs.Switch, {
    checked: fade,
    onChange: setFade
  })), /*#__PURE__*/React.createElement(Row, {
    icon: "phone-call",
    title: "Duck for calls",
    sub: "Lower audio for calls & other apps",
    last: true
  }, /*#__PURE__*/React.createElement(DSs.Switch, {
    checked: focusDuck,
    onChange: setFocusDuck
  }))), /*#__PURE__*/React.createElement(Section, {
    title: "Quick Settings tile"
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "zap",
    title: "Configure tiles",
    sub: "Assign a preset to each shade tile",
    last: true
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "chevron-right",
    style: {
      width: 20,
      height: 20,
      color: 'var(--text-faint)'
    }
  }))), /*#__PURE__*/React.createElement(Section, {
    title: "Sounds"
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "folder-plus",
    title: "Import custom sound",
    sub: "Add your own looping audio",
    last: true
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "chevron-right",
    style: {
      width: 20,
      height: 20,
      color: 'var(--text-faint)'
    }
  }))), /*#__PURE__*/React.createElement(Section, {
    title: "About"
  }, /*#__PURE__*/React.createElement("a", {
    href: "https://github.com/LiukScot/sonari",
    target: "_blank",
    rel: "noopener noreferrer",
    style: {
      textDecoration: 'none'
    }
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "github",
    title: "GitHub",
    sub: "Source code & releases"
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "external-link",
    style: {
      width: 18,
      height: 18,
      color: 'var(--text-faint)'
    }
  }))), /*#__PURE__*/React.createElement("a", {
    href: "https://instagram.com/sonari.app",
    target: "_blank",
    rel: "noopener noreferrer",
    style: {
      textDecoration: 'none'
    }
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "instagram",
    title: "Instagram",
    sub: "@sonari.app"
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "external-link",
    style: {
      width: 18,
      height: 18,
      color: 'var(--text-faint)'
    }
  }))), /*#__PURE__*/React.createElement("a", {
    href: "https://t.me/sonari_app",
    target: "_blank",
    rel: "noopener noreferrer",
    style: {
      textDecoration: 'none'
    }
  }, /*#__PURE__*/React.createElement(Row, {
    icon: "send",
    title: "Telegram",
    sub: "Join the channel",
    last: true
  }, /*#__PURE__*/React.createElement("i", {
    "data-lucide": "external-link",
    style: {
      width: 18,
      height: 18,
      color: 'var(--text-faint)'
    }
  })))), /*#__PURE__*/React.createElement("div", {
    style: {
      textAlign: 'center',
      padding: '4px 8px 4px'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 12,
      color: 'var(--text-muted)',
      lineHeight: 1.5
    }
  }, "This app was inspired by", ' ', /*#__PURE__*/React.createElement("a", {
    href: "https://github.com/rafaelmardojai/blanket",
    target: "_blank",
    rel: "noopener noreferrer",
    style: {
      color: 'var(--accent-solid)',
      fontWeight: 600,
      textDecoration: 'none'
    }
  }, "Blanket"), "."), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 11,
      color: 'var(--text-faint)',
      marginTop: 8
    }
  }, "Sonari \xB7 GPL-3.0 \xB7 sounds CC0"))));
}
Object.assign(window, {
  SettingsScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/sonari-app/SettingsScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/sonari-app/ShadeScreen.jsx
try { (() => {
// ShadeScreen — Android notification shade: media notification + QS tiles.
// This is Sonari's differentiator surface (start a mix without opening the app).
const DSh = window.SonariDesignSystem_901849;
function ShadeScreen({
  open,
  onClose,
  playing,
  onPlayToggle,
  mixLabel,
  tiles,
  mainActive,
  onMainTile
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      position: 'absolute',
      inset: 0,
      zIndex: 30,
      pointerEvents: open ? 'auto' : 'none'
    }
  }, /*#__PURE__*/React.createElement("div", {
    onClick: onClose,
    style: {
      position: 'absolute',
      inset: 0,
      background: 'rgba(6,6,9,0.55)',
      backdropFilter: 'blur(2px)',
      WebkitBackdropFilter: 'blur(2px)',
      opacity: open ? 1 : 0,
      transition: 'opacity var(--dur-base) var(--ease-out)'
    }
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      padding: '46px 14px 22px',
      background: 'linear-gradient(180deg, rgba(14,14,16,0.96) 0%, rgba(14,14,16,0.99) 100%)',
      backdropFilter: 'blur(20px)',
      WebkitBackdropFilter: 'blur(20px)',
      borderBottomLeftRadius: 28,
      borderBottomRightRadius: 28,
      borderBottom: '1px solid var(--line-2)',
      transform: open ? 'translateY(0)' : 'translateY(-104%)',
      transition: 'transform var(--dur-slow) var(--ease-soft)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 10,
      marginBottom: 16,
      flexWrap: 'wrap'
    }
  }, /*#__PURE__*/React.createElement(DSh.QuickTile, {
    label: "Sonari",
    sub: playing ? `${mixLabel}` : 'Tap to start',
    icon: "audio-lines",
    active: mainActive,
    onClick: onMainTile,
    style: {
      flex: '1 1 46%'
    }
  }), tiles.map(t => /*#__PURE__*/React.createElement(DSh.QuickTile, {
    key: t.id,
    label: t.name,
    icon: t.icon,
    active: false,
    sub: "Preset tile",
    style: {
      flex: '1 1 46%'
    }
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      borderRadius: 'var(--r-lg)',
      padding: 14,
      background: 'var(--surface-card)',
      border: '1px solid var(--border-default)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 12
    }
  }, /*#__PURE__*/React.createElement("img", {
    src: "../../assets/sonari-icon.svg",
    width: "44",
    height: "44",
    style: {
      borderRadius: 10
    },
    alt: ""
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 'var(--t-label)',
      fontWeight: 700,
      color: 'var(--text-strong)'
    }
  }, "Sonari"), /*#__PURE__*/React.createElement("div", {
    style: {
      fontSize: 12,
      color: 'var(--text-muted)',
      marginTop: 1
    }
  }, playing ? mixLabel : 'Paused')), /*#__PURE__*/React.createElement(DSh.IconButton, {
    icon: playing ? 'pause' : 'play',
    tone: "raised",
    onClick: onPlayToggle
  }), /*#__PURE__*/React.createElement(DSh.IconButton, {
    icon: "x",
    onClick: onClose,
    "aria-label": "Dismiss"
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      justifyContent: 'center',
      marginTop: 12
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: 36,
      height: 4,
      borderRadius: 2,
      background: 'var(--line-strong)'
    }
  }))));
}
Object.assign(window, {
  ShadeScreen
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/sonari-app/ShadeScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/sonari-app/data.js
try { (() => {
// Sonari sample data — the 15 built-in CC0 sounds (from Blanket) + presets.
window.SONARI_SOUNDS = [{
  id: 'rain',
  name: 'Rain',
  icon: 'cloud-rain'
}, {
  id: 'storm',
  name: 'Storm',
  icon: 'cloud-lightning'
}, {
  id: 'wind',
  name: 'Wind',
  icon: 'wind'
}, {
  id: 'waves',
  name: 'Waves',
  icon: 'waves'
}, {
  id: 'stream',
  name: 'Stream',
  icon: 'droplet'
}, {
  id: 'birds',
  name: 'Birds',
  icon: 'bird'
}, {
  id: 'night',
  name: 'Summer night',
  icon: 'moon-star'
}, {
  id: 'crickets',
  name: 'Crickets',
  icon: 'bug'
}, {
  id: 'train',
  name: 'Train',
  icon: 'train-front'
}, {
  id: 'boat',
  name: 'Boat',
  icon: 'sailboat'
}, {
  id: 'city',
  name: 'City',
  icon: 'building-2'
}, {
  id: 'coffee',
  name: 'Coffee shop',
  icon: 'coffee'
}, {
  id: 'fire',
  name: 'Fireplace',
  icon: 'flame'
}, {
  id: 'white',
  name: 'White noise',
  icon: 'audio-waveform'
}, {
  id: 'pink',
  name: 'Pink noise',
  icon: 'radio'
}];
window.SONARI_PRESETS = [{
  id: 'afterrain',
  name: 'afterRain',
  icon: 'cloud-drizzle',
  tile: true,
  mix: {
    rain: 70,
    stream: 38,
    birds: 24
  }
}, {
  id: 'focus',
  name: 'Deep focus',
  icon: 'brain',
  tile: true,
  mix: {
    rain: 55,
    coffee: 48,
    white: 30
  }
}, {
  id: 'sleep',
  name: 'Night sleep',
  icon: 'moon',
  tile: false,
  mix: {
    wind: 40,
    night: 52,
    crickets: 30
  }
}, {
  id: 'voyage',
  name: 'Voyage',
  icon: 'sailboat',
  tile: false,
  mix: {
    waves: 66,
    boat: 44,
    wind: 28
  }
}];
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/sonari-app/data.js", error: String((e && e.message) || e) }); }

__ds_ns.Badge = __ds_scope.Badge;

__ds_ns.Button = __ds_scope.Button;

__ds_ns.IconButton = __ds_scope.IconButton;

__ds_ns.PlayButton = __ds_scope.PlayButton;

__ds_ns.SegmentedControl = __ds_scope.SegmentedControl;

__ds_ns.Switch = __ds_scope.Switch;

__ds_ns.VolumeSlider = __ds_scope.VolumeSlider;

__ds_ns.QuickTile = __ds_scope.QuickTile;

__ds_ns.SoundCard = __ds_scope.SoundCard;

})();
