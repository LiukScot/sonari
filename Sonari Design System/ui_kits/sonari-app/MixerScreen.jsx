// MixerScreen — Sonari home: preset chips, sound grid, playback bar.
const DS = window.SonariDesignSystem_901849;

function PresetChip({ preset, active, onClick }) {
  return (
    <button onClick={onClick} style={{
      display: 'inline-flex', alignItems: 'center', gap: 8, flex: '0 0 auto',
      height: 38, padding: '0 14px 0 12px', borderRadius: 'var(--r-pill)', cursor: 'pointer',
      border: `1px solid ${active ? 'transparent' : 'var(--border-default)'}`,
      background: active ? 'var(--accent-016)' : 'var(--surface-card)',
      color: active ? 'var(--accent-solid)' : 'var(--text-muted)',
      fontFamily: 'var(--font-sans)', fontSize: 'var(--t-caption)', fontWeight: 600,
      WebkitTapHighlightColor: 'transparent',
    }}>
      <i data-lucide={preset.icon} style={{ width: 15, height: 15 }} />
      {preset.name}
      {preset.tile && <i data-lucide="zap" style={{ width: 12, height: 12, opacity: 0.8 }} />}
    </button>
  );
}

function MixerScreen({
  sounds, mix, setVol, playing, onPlayToggle,
  presets, activePreset, onPreset, onOpenTimer, onOpenSettings, timerLabel,
}) {
  const activeCount = Object.values(mix).filter((v) => v > 0).length;
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: 'var(--surface-app)' }}>
      {/* App title */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 11, padding: '18px 18px 14px' }}>
        <img src="../../assets/sonari-mark.svg" width="30" height="30" alt="" />
        <div style={{ fontSize: 'var(--t-title)', fontWeight: 800, color: 'var(--text-strong)',
          letterSpacing: '-0.02em' }}>Sonari</div>
      </div>

      {/* Sound grid */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 18px 18px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 'var(--grid-gap)' }}>
          {sounds.map((s) => (
            <DS.SoundCard key={s.id} name={s.name} icon={s.icon} volume={mix[s.id] || 0}
              onToggle={() => setVol(s.id, (mix[s.id] || 0) > 0 ? 0 : 55)}
              onVolume={(v) => setVol(s.id, v)} />
          ))}
        </div>
      </div>

      {/* Bottom playback bar */}
      <div style={{ flex: '0 0 auto', padding: '12px 18px calc(12px + env(safe-area-inset-bottom))',
        background: 'var(--ink-1)', borderTop: '1px solid var(--line-1)',
        borderTopLeftRadius: 'var(--r-xl)', borderTopRightRadius: 'var(--r-xl)',
        boxShadow: '0 -8px 30px rgba(0,0,0,0.4)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
              <span style={{ fontSize: 'var(--t-caption)', fontWeight: 600, color: 'var(--text-body)' }}>
                {activeCount > 0 ? `${activeCount} sound${activeCount > 1 ? 's' : ''} mixing` : 'No sounds yet'}
              </span>
              {timerLabel && <span style={{ fontFamily: 'var(--font-mono)', fontSize: 11,
                color: 'var(--accent-solid)' }}>{timerLabel}</span>}
            </div>
          </div>
          <DS.IconButton icon={timerLabel ? 'timer' : 'moon'} tone="raised" active={!!timerLabel}
            onClick={onOpenTimer} aria-label="Sleep timer" />
          <DS.PlayButton playing={playing} size={62} onClick={onPlayToggle} />
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { MixerScreen });
