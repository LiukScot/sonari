// PresetsScreen — saved mixes, each assignable to a Quick Settings tile.
// Cards use grouped corner radii (Android Settings pattern): 14dp outer, 4dp inner on touching sides, 2dp gap.
const DSp = window.SonariDesignSystem_901849;

function groupedRadius(isFirst, isLast) {
  const outer = 'var(--r-md)';
  const inner = '4px';
  const tl = isFirst ? outer : inner;
  const tr = isFirst ? outer : inner;
  const br = isLast  ? outer : inner;
  const bl = isLast  ? outer : inner;
  return `${tl} ${tr} ${br} ${bl}`;
}

function PresetRow({ preset, sounds, onPlay, onToggleTile, playingId, isFirst, isLast }) {
  const ids = Object.keys(preset.mix);
  const isPlaying = playingId === preset.id;
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', gap: 12, padding: 14,
      borderRadius: groupedRadius(isFirst, isLast),
      background: isPlaying ? 'var(--surface-card-grad)' : 'var(--surface-card)',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <div style={{ width: 44, height: 44, borderRadius: 'var(--r-sm)', display: 'grid', placeItems: 'center',
          background: 'var(--accent-016)', color: 'var(--accent-solid)', flex: '0 0 auto' }}>
          <i data-lucide={preset.icon} style={{ width: 22, height: 22 }} />
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 'var(--t-label)', fontWeight: 700, color: 'var(--text-strong)' }}>{preset.name}</div>
          <div style={{ display: 'flex', gap: 8, marginTop: 5, color: 'var(--text-faint)' }}>
            {ids.map((id) => {
              const s = sounds.find((x) => x.id === id);
              return s ? <i key={id} data-lucide={s.icon} style={{ width: 15, height: 15 }} /> : null;
            })}
          </div>
        </div>
        <DSp.IconButton icon={isPlaying ? 'pause' : 'play'} tone="raised" onClick={() => onPlay(preset.id)} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        paddingTop: 10, borderTop: '1px solid var(--line-1)' }}>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 7, fontSize: 'var(--t-caption)',
          color: 'var(--text-muted)', fontWeight: 600 }}>
          <i data-lucide="zap" style={{ width: 14, height: 14, color: preset.tile ? 'var(--accent-solid)' : 'var(--text-faint)' }} />
          Quick Settings tile
        </span>
        <DSp.Switch checked={preset.tile} onChange={() => onToggleTile(preset.id)} />
      </div>
    </div>
  );
}

function PresetsScreen({ presets, sounds, onPlay, onToggleTile, playingId }) {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: 'var(--surface-app)' }}>
      <div style={{ padding: '16px 18px 8px' }}>
        <div style={{ fontSize: 'var(--t-title)', fontWeight: 800, color: 'var(--text-strong)', letterSpacing: '-0.02em' }}>Presets</div>
        <div style={{ fontSize: 'var(--t-caption)', color: 'var(--text-muted)', marginTop: 3 }}>
          Save a mix, then start it from the shade. Long-press to delete.
        </div>
      </div>
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 18px 18px', display: 'flex', flexDirection: 'column', gap: 20 }}>
        {/* Presets group — 2dp gap between adjacent cards */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {presets.map((p, i) => (
            <PresetRow
              key={p.id}
              preset={p}
              sounds={sounds}
              onPlay={onPlay}
              onToggleTile={onToggleTile}
              playingId={playingId}
              isFirst={i === 0}
              isLast={i === presets.length - 1}
            />
          ))}
        </div>
        <DSp.Button variant="secondary" icon="plus" full>New preset from current mix</DSp.Button>
      </div>
    </div>
  );
}

Object.assign(window, { PresetsScreen });
