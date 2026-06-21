// ShadeScreen — Android notification shade: media notification + QS tiles.
// This is Sonari's differentiator surface (start a mix without opening the app).
const DSh = window.SonariDesignSystem_901849;

function ShadeScreen({ open, onClose, playing, onPlayToggle, mixLabel, tiles, mainActive, onMainTile }) {
  return (
    <div style={{
      position: 'absolute', inset: 0, zIndex: 30, pointerEvents: open ? 'auto' : 'none',
    }}>
      {/* scrim */}
      <div onClick={onClose} style={{
        position: 'absolute', inset: 0, background: 'rgba(6,6,9,0.55)',
        backdropFilter: 'blur(2px)', WebkitBackdropFilter: 'blur(2px)',
        opacity: open ? 1 : 0, transition: 'opacity var(--dur-base) var(--ease-out)',
      }} />
      {/* sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, top: 0, padding: '46px 14px 22px',
        background: 'linear-gradient(180deg, rgba(14,14,16,0.96) 0%, rgba(14,14,16,0.99) 100%)',
        backdropFilter: 'blur(20px)', WebkitBackdropFilter: 'blur(20px)',
        borderBottomLeftRadius: 28, borderBottomRightRadius: 28,
        borderBottom: '1px solid var(--line-2)',
        transform: open ? 'translateY(0)' : 'translateY(-104%)',
        transition: 'transform var(--dur-slow) var(--ease-soft)',
      }}>
        {/* QS tile row */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap' }}>
          <DSh.QuickTile label="Sonari" sub={playing ? `${mixLabel}` : 'Tap to start'}
            icon="audio-lines" active={mainActive} onClick={onMainTile} style={{ flex: '1 1 46%' }} />
          {tiles.map((t) => (
            <DSh.QuickTile key={t.id} label={t.name} icon={t.icon}
              active={false} sub="Preset tile" style={{ flex: '1 1 46%' }} />
          ))}
        </div>

        {/* Media notification */}
        <div style={{ borderRadius: 'var(--r-lg)', padding: 14, background: 'var(--surface-card)',
          border: '1px solid var(--border-default)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <img src="../../assets/sonari-icon.svg" width="44" height="44" style={{ borderRadius: 10 }} alt="" />
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 'var(--t-label)', fontWeight: 700, color: 'var(--text-strong)' }}>Sonari</div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 1 }}>
                {playing ? mixLabel : 'Paused'}
              </div>
            </div>
            <DSh.IconButton icon={playing ? 'pause' : 'play'} tone="raised" onClick={onPlayToggle} />
            <DSh.IconButton icon="x" onClick={onClose} aria-label="Dismiss" />
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}>
          <div style={{ width: 36, height: 4, borderRadius: 2, background: 'var(--line-strong)' }} />
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { ShadeScreen });
