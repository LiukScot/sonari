// SettingsScreen — audio behaviour and the configurable Quick Tile.
const DSs = window.SonariDesignSystem_901849;

function Row({ icon, title, sub, children, last }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '14px 0',
      borderBottom: last ? 'none' : '1px solid var(--line-1)' }}>
      <div style={{ width: 38, height: 38, borderRadius: 'var(--r-sm)', flex: '0 0 auto',
        display: 'grid', placeItems: 'center', background: 'var(--surface-raised)', color: 'var(--accent-solid)' }}>
        <i data-lucide={icon} style={{ width: 19, height: 19 }} />
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 'var(--t-label)', fontWeight: 600, color: 'var(--text-strong)' }}>{title}</div>
        {sub && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>{sub}</div>}
      </div>
      <div style={{ flex: '0 0 auto' }}>{children}</div>
    </div>
  );
}

function Section({ title, children }) {
  return (
    <div style={{ marginBottom: 18 }}>
      <div style={{ fontSize: 'var(--t-micro)', letterSpacing: 'var(--track-caps)', textTransform: 'uppercase',
        color: 'var(--text-faint)', fontWeight: 700, margin: '0 0 6px 2px' }}>{title}</div>
      <div style={{ padding: '0 14px', borderRadius: 'var(--r-md)', background: 'var(--surface-card)',
        border: '1px solid var(--border-default)' }}>{children}</div>
    </div>
  );
}

function SettingsScreen({ fade, setFade, focusDuck, setFocusDuck }) {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: 'var(--surface-app)' }}>
      <div style={{ padding: '16px 18px 8px' }}>
        <div style={{ fontSize: 'var(--t-title)', fontWeight: 800, color: 'var(--text-strong)', letterSpacing: '-0.02em' }}>Settings</div>
      </div>
      <div style={{ flex: 1, overflowY: 'auto', padding: '10px 18px 18px' }}>
        <Section title="Playback">
          <Row icon="audio-lines" title="Fade in / out" sub="~1s when you play or pause">
            <DSs.Switch checked={fade} onChange={setFade} />
          </Row>
          <Row icon="phone-call" title="Duck for calls" sub="Lower audio for calls & other apps" last>
            <DSs.Switch checked={focusDuck} onChange={setFocusDuck} />
          </Row>
        </Section>

        <Section title="Quick Settings tile">
          <Row icon="zap" title="Configure tiles" sub="Assign a preset to each shade tile" last>
            <i data-lucide="chevron-right" style={{ width: 20, height: 20, color: 'var(--text-faint)' }} />
          </Row>
        </Section>

        <Section title="Sounds">
          <Row icon="folder-plus" title="Import custom sound" sub="Add your own looping audio" last>
            <i data-lucide="chevron-right" style={{ width: 20, height: 20, color: 'var(--text-faint)' }} />
          </Row>
        </Section>

        <Section title="About">
          <a href="https://github.com/LiukScot/sonari" target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
            <Row icon="github" title="GitHub" sub="Source code & releases">
              <i data-lucide="external-link" style={{ width: 18, height: 18, color: 'var(--text-faint)' }} />
            </Row>
          </a>
          <a href="https://instagram.com/sonari.app" target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
            <Row icon="instagram" title="Instagram" sub="@sonari.app">
              <i data-lucide="external-link" style={{ width: 18, height: 18, color: 'var(--text-faint)' }} />
            </Row>
          </a>
          <a href="https://t.me/sonari_app" target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
            <Row icon="send" title="Telegram" sub="Join the channel" last>
              <i data-lucide="external-link" style={{ width: 18, height: 18, color: 'var(--text-faint)' }} />
            </Row>
          </a>
        </Section>

        <div style={{ textAlign: 'center', padding: '4px 8px 4px' }}>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.5 }}>
            This app was inspired by{' '}
            <a href="https://github.com/rafaelmardojai/blanket" target="_blank" rel="noopener noreferrer"
              style={{ color: 'var(--accent-solid)', fontWeight: 600, textDecoration: 'none' }}>Blanket</a>.
          </div>
          <div style={{ fontSize: 11, color: 'var(--text-faint)', marginTop: 8 }}>Sonari · GPL-3.0 · sounds CC0</div>
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { SettingsScreen });
