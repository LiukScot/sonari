// MixerScreen — Sonari home: sound groups, edit mode.
const DS = window.SonariDesignSystem_901849;

const initGroups = (sounds) => {
  const half = Math.ceil(sounds.length / 2);
  return [
    { id: 'g_nature', name: 'Nature',  soundIds: sounds.slice(0, half).map(s => s.id) },
    { id: 'g_ambient', name: 'Ambient', soundIds: sounds.slice(half).map(s => s.id) },
  ];
};

function GroupHeader({ group, editMode, onRenameStart, onDelete, canDelete, onDragStart, onDragEnd }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 10 }}>
      {editMode && (
        <i data-lucide="grip-vertical"
          draggable={true}
          onDragStart={onDragStart}
          onDragEnd={onDragEnd}
          style={{ width: 18, height: 18, color: 'var(--text-faint)', flex: '0 0 auto', cursor: 'grab' }} />
      )}
      <div className={editMode ? 'sonari-wiggle' : ''}
        style={{ flex: 1, fontSize: 'var(--t-micro)', textTransform: 'uppercase',
          letterSpacing: 'var(--track-caps)', color: 'var(--text-faint)', fontWeight: 700 }}>
        {group.name}
      </div>
      {editMode && (
        <>
          <button type="button" onClick={onRenameStart}
            aria-label={`Rename ${group.name}`}
            style={{ width: 28, height: 28, border: 'none', background: 'var(--surface-raised)',
              borderRadius: 'var(--r-sm)', cursor: 'pointer', display: 'grid', placeItems: 'center',
              color: 'var(--text-muted)' }}>
            <i data-lucide="pencil" aria-hidden="true" style={{ width: 14, height: 14 }} />
          </button>
          {canDelete && (
            <button type="button" onClick={onDelete}
              aria-label={`Delete ${group.name}`}
              style={{ width: 28, height: 28, border: 'none', background: 'var(--surface-raised)',
                borderRadius: 'var(--r-sm)', cursor: 'pointer', display: 'grid', placeItems: 'center',
                color: 'var(--text-faint)' }}>
              <i data-lucide="trash-2" aria-hidden="true" style={{ width: 14, height: 14 }} />
            </button>
          )}
        </>
      )}
    </div>
  );
}

function RenameSheet({ open, value, onChange, onConfirm, onClose }) {
  if (!open) return null;

  return (
    <div style={{ position: 'absolute', inset: 0, zIndex: 28 }}>
      <div onClick={onClose}
        style={{ position: 'absolute', inset: 0, background: 'rgba(6,6,9,0.55)',
          transition: 'opacity 180ms' }} />
      <div role="dialog" aria-labelledby="rename-dialog-title" aria-modal="true"
        style={{ position: 'absolute', left: 0, right: 0, bottom: 0, padding: '16px 18px 32px',
        background: 'var(--ink-1)', borderTopLeftRadius: 24, borderTopRightRadius: 24,
        borderTop: '1px solid var(--line-2)',
        transition: 'transform 280ms var(--ease-soft)' }}>
        <div style={{ display: 'flex', justifyContent: 'center', padding: '0 0 16px' }}>
          <div style={{ width: 36, height: 4, borderRadius: 2, background: 'var(--line-strong)' }} />
        </div>
        <div id="rename-dialog-title" style={{ fontSize: 'var(--t-heading)', fontWeight: 700, color: 'var(--text-strong)', marginBottom: 14 }}>
          Rename group
        </div>
        <input value={value} onChange={e => onChange(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && onConfirm()}
          autoFocus
          aria-label="Group name"
          style={{ width: '100%', height: 48, background: 'var(--surface-raised)',
            border: '1px solid var(--accent-solid)', borderRadius: 'var(--r-md)',
            padding: '0 14px', color: 'var(--text-strong)', fontFamily: 'var(--font-sans)',
            fontSize: 'var(--t-label)', boxSizing: 'border-box' }} />
        <div style={{ display: 'flex', gap: 10, marginTop: 14 }}>
          <DS.Button variant="ghost" full onClick={onClose}>Cancel</DS.Button>
          <DS.Button variant="primary" full onClick={onConfirm}>Rename</DS.Button>
        </div>
      </div>
    </div>
  );
}

function MixerScreen({
  sounds, mix, setVol, playing, onPlayToggle,
  presets, activePreset, onPreset, onOpenTimer, onOpenSettings, timerLabel,
}) {
  const [editMode, setEditMode] = React.useState(false);
  const [groups, setGroups] = React.useState(() => initGroups(sounds));
  const [groupsSnapshot, setGroupsSnapshot] = React.useState(null);
  const [renamingGroup, setRenamingGroup] = React.useState(null);
  const [renameVal, setRenameVal] = React.useState('');
  const [deletingGroup, setDeletingGroup] = React.useState(null);
  const [draggedGroup, setDraggedGroup] = React.useState(null);
  const [draggedSound, setDraggedSound] = React.useState(null);

  const activeCount = Object.values(mix).filter(v => v > 0).length;

  const enterEdit = () => {
    setGroupsSnapshot(groups);
    setEditMode(true);
  };
  const cancelEdit = () => {
    if (groupsSnapshot) setGroups(groupsSnapshot);
    setGroupsSnapshot(null);
    setEditMode(false);
    setRenamingGroup(null);
  };
  const confirmEdit = () => {
    setGroupsSnapshot(null);
    setEditMode(false);
    setRenamingGroup(null);
  };

  const startRename = (group) => { setRenamingGroup(group.id); setRenameVal(group.name); };
  const applyRename = () => {
    if (renameVal.trim()) {
      setGroups(gs => gs.map(g => g.id === renamingGroup ? { ...g, name: renameVal.trim() } : g));
    }
    setRenamingGroup(null);
  };

  const confirmDeleteGroup = (groupId) => {
    setGroups(gs => {
      const idx = gs.findIndex(g => g.id === groupId);
      const targetIdx = idx < gs.length - 1 ? idx + 1 : idx - 1;
      return gs
        .map((g, i) => i === targetIdx
          ? { ...g, soundIds: [...g.soundIds, ...gs[idx].soundIds] }
          : g)
        .filter(g => g.id !== groupId);
    });
    setDeletingGroup(null);
  };

  const addGroup = () => {
    const id = 'g_' + Math.random().toString(36).slice(2, 7);
    const newGroup = { id, name: 'New group', soundIds: [] };
    setGroups(gs => [...gs, newGroup]);
    setRenamingGroup(id);
    setRenameVal('New group');
  };

  const handleGroupDragStart = (groupId) => (e) => {
    setDraggedGroup(groupId);
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleGroupDragEnd = () => {
    setDraggedGroup(null);
  };

  const handleGroupDrop = (targetGroupId) => (e) => {
    e.preventDefault();
    if (draggedGroup && draggedGroup !== targetGroupId) {
      setGroups(gs => {
        const fromIdx = gs.findIndex(g => g.id === draggedGroup);
        const toIdx = gs.findIndex(g => g.id === targetGroupId);
        if (fromIdx === -1 || toIdx === -1) return gs;
        const newGroups = [...gs];
        const [moved] = newGroups.splice(fromIdx, 1);
        newGroups.splice(toIdx, 0, moved);
        return newGroups;
      });
    }
    setDraggedGroup(null);
  };

  const handleSoundDragStart = (groupId, soundId) => (e) => {
    setDraggedSound({ groupId, soundId });
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleSoundDragEnd = () => {
    setDraggedSound(null);
  };

  const handleSoundDrop = (targetGroupId, targetSoundId) => (e) => {
    e.preventDefault();
    if (draggedSound) {
      setGroups(gs => gs.map(g => {
        if (g.id === draggedSound.groupId) {
          return { ...g, soundIds: g.soundIds.filter(id => id !== draggedSound.soundId) };
        }
        if (g.id === targetGroupId) {
          const targetIdx = g.soundIds.indexOf(targetSoundId);
          const newSoundIds = [...g.soundIds];
          newSoundIds.splice(targetIdx, 0, draggedSound.soundId);
          return { ...g, soundIds: newSoundIds };
        }
        return g;
      }));
    }
    setDraggedSound(null);
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column',
      background: 'var(--surface-app)', position: 'relative', overflow: 'hidden' }}>

      {editMode && <style>{`
        @keyframes sonari-wiggle {
          0%   { transform: rotate(-1.3deg) scale(1.01); }
          100% { transform: rotate( 1.3deg) scale(1.01); }
        }
        .sonari-wiggle {
          animation: sonari-wiggle 0.17s ease-in-out infinite alternate;
          transform-origin: center;
        }
      `}</style>}

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 11, padding: '18px 18px 14px' }}>
        <img src="../../assets/sonari-mark.svg" width="30" height="30" alt="" />
        <div style={{ flex: 1, fontSize: 'var(--t-title)', fontWeight: 800,
          color: 'var(--text-strong)', letterSpacing: '-0.02em' }}>Sonari</div>
      </div>

      {/* Sound groups */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 18px 88px' }}>
        {groups.map((group, gi) => {
          const soundsById = Object.fromEntries(sounds.map(s => [s.id, s]));
          const groupSounds = group.soundIds.map(id => soundsById[id]).filter(Boolean);
          return (
            <div key={group.id} style={{ marginBottom: 20 }}
              onDragOver={(e) => editMode && e.preventDefault()}
              onDrop={handleGroupDrop(group.id)}>
              <GroupHeader
                group={group}
                editMode={editMode}
                canDelete={groups.length > 1}
                onRenameStart={() => startRename(group)}
                onDelete={() => setDeletingGroup(group)}
                onDragStart={handleGroupDragStart(group.id)}
                onDragEnd={handleGroupDragEnd} />
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 'var(--grid-gap)' }}>
                {groupSounds.map((s, si) => (
                  <div key={s.id} className={editMode ? 'sonari-wiggle' : ''}
                    style={{ animationDelay: `${(gi * 3 + si) * 25}ms`, position: 'relative' }}
                    onDragOver={(e) => editMode && e.preventDefault()}
                    onDrop={handleSoundDrop(group.id, s.id)}>
                    <DS.SoundCard name={s.name} icon={s.icon} volume={mix[s.id] || 0}
                      onToggle={() => !editMode && setVol(s.id, (mix[s.id] || 0) > 0 ? 0 : 55)}
                      onVolume={v => !editMode && setVol(s.id, v)} />
                    {editMode && (
                      <div draggable={true}
                        onDragStart={handleSoundDragStart(group.id, s.id)}
                        onDragEnd={handleSoundDragEnd}
                        style={{ position: 'absolute', inset: 0, borderRadius: 'var(--r-md)',
                          cursor: 'grab', display: 'grid', placeItems: 'center',
                          background: 'rgba(0,0,0,0.12)' }}>
                        <i data-lucide="grip" style={{ width: 22, height: 22, color: 'rgba(255,255,255,0.45)' }} />
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          );
        })}

        {editMode && (
          <div style={{ marginTop: 4 }}>
            <DS.Button variant="secondary" icon="plus" full onClick={addGroup}>Add group</DS.Button>
          </div>
        )}
      </div>

      {/* Bottom bar — floats over content, no background */}
      <div style={{ position: 'absolute', bottom: 0, right: 0, padding: '12px 18px' }}>
        {editMode ? (
          <div style={{ position: 'relative', display: 'flex', gap: 10 }}>
            <DS.Button variant="ghost" full onClick={cancelEdit}>Cancel</DS.Button>
            <DS.Button variant="primary" full onClick={confirmEdit}>Done</DS.Button>
          </div>
        ) : (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 12 }}>
            <div style={{ filter: 'drop-shadow(0 0 14px rgba(0,0,0,1))' }}>
              <DS.IconButton icon="pencil" tone="raised" size="lg" onClick={enterEdit} aria-label="Edit layout" />
            </div>
            <div style={{ filter: 'drop-shadow(0 0 14px rgba(0,0,0,1))' }}>
              <DS.IconButton icon={timerLabel ? 'timer' : 'moon'} tone="raised" size="lg" active={!!timerLabel}
                onClick={onOpenTimer} aria-label="Sleep timer" />
            </div>
            <div style={{ filter: 'drop-shadow(0 0 14px rgba(0,0,0,1))' }}>
              <DS.PlayButton playing={playing} size={52} onClick={onPlayToggle} />
            </div>
          </div>
        )}
      </div>

      <RenameSheet
        open={renamingGroup !== null}
        value={renameVal}
        onChange={setRenameVal}
        onConfirm={applyRename}
        onClose={() => setRenamingGroup(null)} />

      {deletingGroup && (
        <div style={{ position: 'absolute', inset: 0, zIndex: 30, display: 'grid', placeItems: 'center' }}>
          <div onClick={() => setDeletingGroup(null)}
            style={{ position: 'absolute', inset: 0, background: 'rgba(6,6,9,0.65)' }} />
          <div role="alertdialog" aria-labelledby="delete-dialog-title" aria-describedby="delete-dialog-desc"
            style={{ position: 'relative', width: 'calc(100% - 64px)', maxWidth: 340, background: 'var(--ink-1)',
              borderRadius: 'var(--r-lg)', padding: '20px 20px 16px', border: '1px solid var(--line-2)' }}>
            <div id="delete-dialog-title" style={{ fontSize: 'var(--t-heading)', fontWeight: 700, color: 'var(--text-strong)', marginBottom: 10 }}>
              Delete group?
            </div>
            <div id="delete-dialog-desc" style={{ fontSize: 'var(--t-body)', color: 'var(--text-body)', marginBottom: 18 }}>
              "{deletingGroup.name}" and its layout will be removed. Sounds are not deleted.
            </div>
            <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
              <DS.Button variant="ghost" onClick={() => setDeletingGroup(null)}>Cancel</DS.Button>
              <DS.Button variant="primary" onClick={() => confirmDeleteGroup(deletingGroup.id)}>Delete</DS.Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

Object.assign(window, { MixerScreen });
