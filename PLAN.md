# Sonari — Piano di progetto

> Nome: **Sonari** (bloccato). Package: `io.github.liukscot.sonari`.
> App Android **mixer di suoni di sottofondo** (pioggia, onde, caffè, camino…)
> per concentrazione, studio, relax *e* sonno — ispirata a
> [Blanket](https://github.com/rafaelmardojai/blanket) (GNOME). Non è legata
> alla notte: il posizionamento è "il sottofondo della tua giornata".
> Feature distintiva (per i cloni di Blanket): **Quick Settings Tile** per
> avviare i suoni dal menu a tendina, senza aprire l'app.
>
> ⚠️ **Prior art (verificato giugno 2026):** la Quick Tile è **assente** dai
> cloni di Blanket (`blankee`, `Kaze`) e dalle grandi app mainstream, ma **non
> è inedita**. Due app la hanno già:
> - **Easy Noise** (`com.cliambrown.easynoise`) — FOSS su F-Droid, ha widget +
>   Quick Settings tile + 7 suoni. → riferimento pratico per la nostra Tile.
> - **Ambient Music** (`com.sourajitk.ambient_music`) — Play Store, con QS tile.
>
> Quindi: la Tile ci differenzia dai cloni di Blanket e ci mette al livello
> delle poche app fatte bene — non è un primato mondiale.

## 1. Visione

Un mixer di suoni di sottofondo. L'utente attiva più suoni insieme
(pioggia + onde + caffè), ognuno con il proprio volume, e li lascia in loop
mentre lavora, studia o dorme. Deve continuare a suonare a schermo spento e
poter essere avviato/fermato istantaneamente dalla Quick Tile.

Tre pilastri:
1. **Multi-mix** — più suoni contemporaneamente, volume per suono (modello Blanket).
2. **Quick Settings Tile** — toggle play/pausa dell'ultimo mix dal menu a tendina.
3. **Sopravvive allo schermo spento** — Foreground Service obbligatorio.

## 2. Panorama competitivo e differenziazione

Analisi giugno 2026. **Osservazione chiave:** ogni concorrente ha *o* la
personalizzazione *o* la quick tile, **mai entrambe fatte bene**. La nicchia
vuota è l'incrocio.

| App | Pro | Cosa manca / limite |
|-----|-----|---------------------|
| **Blanket** (GNOME, originale) | multi-mix personalizzabile, preset, suoni CC0 | è desktop Linux, **non esiste su Android** |
| **blankee** (Android, FOSS) | UI multi-mix buona, foreground service, notifica | loop con click, no audio focus, **no quick tile** |
| **Kaze** (Android, FOSS) | minimale | troppo primitiva, niente service vero, no tile |
| **Easy Noise** (Android, FOSS) | **ha la quick tile** | non mantenuta da ~4 anni, molto spartana, troppo semplice, pochi suoni, **mix non personalizzabile** |
| **Ambient Music** (Android, Play Store) | quick tile | concentrata **solo** sulle tile, tile **non personalizzabili**, non è un vero mixer |
| **White Noise / BetterSleep** (mainstream) | tanti suoni, feature | no quick tile, bloated, ads/paywall |

### 2.1 Come ci distinguiamo (il fossato)
Nessuno mette insieme **mixer personalizzabile + quick tile configurabile +
app moderna e mantenuta**. È lì che ci piazziamo. Feature che ci definiscono:

1. **Multi-mix personalizzabile** (volume per suono) — come Blanket, meglio
   delle app tile-only (Ambient Music, Easy Noise).
2. **Quick Tile *configurabile*** — l'utente sceglie *quale* preset/mix avvia la
   tile (Ambient Music ce l'ha ma rigida → noi no). Eventualmente più tile per
   preset diversi. ← questo è l'incrocio che nessuno fa.
3. **Loop senza cuciture** (Media3 ExoPlayer) — meglio di blankee/Easy Noise.
4. **Robustezza**: audio focus, sleep timer, fade out — assenti negli spartani.
5. **Moderna e mantenuta**: Compose + Media3, codice vivo (Easy Noise è ferma da
   ~4 anni).

> In una riga: *"Blanket per Android, ma con una quick tile che configuri tu."*

### 2.2 Riferimento di codice: itsPronay/blankee
Clone Android di Blanket (Kotlin, Compose, 25★). Abbiamo letto il suo codice
come riferimento. **Non lo forkiamo** (licenza + package altrui), ne replichiamo
i pattern buoni e correggiamo i difetti. (Per la tile, il riferimento pratico è
invece **Easy Noise**, vedi §3.4.)

Da imitare:
- 1 player per suono, multi-mix = più player attivi con `setVolume` indipendente.
- `GlobalPlaybackState`: unica fonte di verità `canPlay: StateFlow<Boolean>` +
  `togglePlayPause()`, con un **holder statico** raggiungibile da Service/Tile
  fuori da Compose. → è l'aggancio della Tile.
- Foreground Service con notifica media (play/pausa + apri app).
- Persistenza dell'ultimo stato (preset) per il riavvio.

Da correggere:
- **Loop con cuciture**: blankee usa `MediaPlayer.isLooping`, che fa click al
  loop point su molti device → inaccettabile per il sonno. → usiamo ExoPlayer.
- **Preload di tutti i suoni all'avvio** → creazione pigra al primo play.
- **Nessun audio focus** (non si abbassa per chiamate/altre app) → lo gestiamo.
- **MediaSessionCompat legacy + notifica a mano** → sostituiti da Media3.

## 3. Architettura scelta

```
┌─────────────────┐     toggle/intent      ┌──────────────────────────┐
│ QuickTileService │ ─────────────────────▶ │  PlaybackService          │
│ (menu a tendina) │                        │  (Media3 MediaSessionSvc) │
└─────────────────┘                        │  - foreground + notifica  │
┌─────────────────┐     observe/command     │  - audio focus            │
│  Compose UI      │ ◀────────────────────▶ │                          │
│  (griglia suoni) │                        └────────────┬─────────────┘
└─────────────────┘                                      │ comanda
                                              ┌───────────▼────────────┐
                                              │  AudioEngine            │
                                              │  Map<SoundId, ExoPlayer>│
                                              │  loop seamless + volume │
                                              └────────────┬───────────┘
                                                           │ persiste
                                              ┌────────────▼───────────┐
                                              │ DataStore: ultimo mix   │
                                              │ (id suoni + volumi)     │
                                              └────────────────────────┘
```

### 3.1 Motore audio — Media3 ExoPlayer
- Un `ExoPlayer` per suono attivo, creato **pigro** al primo play (non tutti all'avvio).
- Loop senza cuciture: `player.repeatMode = REPEAT_MODE_ONE`.
- Volume per suono: `player.volume = 0f..1f`.
- `AudioAttributes(USAGE_MEDIA, CONTENT_TYPE_MUSIC)` con `handleAudioFocus = true`
  → si abbassa/ferma automaticamente per chiamate e altre app.
- Multi-mix = N player che suonano insieme; Android li missa a livello di sistema.
- **Fallback se vogliamo zero dipendenze:** `MediaPlayer` (come blankee), ma con
  loop udibile imperfetto. Sconsigliato per un'app del sonno.

### 3.2 Stato di riproduzione — unica fonte di verità
- `canPlay: StateFlow<Boolean>` + lista dei suoni attivi con i loro volumi.
- Esposto sia alla UI (Compose `collectAsState`) sia al Service/Tile (holder
  statico o binder). La Tile **non** ha il suo stato: legge questo.

### 3.3 Foreground Service — Media3 `MediaSessionService`
Perché serve: senza Foreground Service Android uccide l'audio a schermo spento.
Media3 dà quasi gratis:
- notifica media con play/pausa (e tap → apre l'app),
- gestione del lifecycle del foreground,
- integrazione con cuffie/Bluetooth/auto.

### 3.4 Quick Settings Tile — il pezzo differenziante
Ogni `TileService` va dichiarata nel manifest con `BIND_QUICK_SETTINGS_TILE`.

**Modello a due livelli:**
- **1 tile principale** (`MainTileService`): toggle play/pausa dell'ultimo
  mix. Feature base, Fase 2.
- **5 slot-tile** (`PresetTileService1..5`): spenti di default; l'utente li
  aggiunge dal menu a tendina e assegna a ognuno un preset dall'app. Fase 6.

> ⚠️ **Vincolo Android:** le tile NON si creano a runtime — ogni `TileService`
> è dichiarata staticamente a compile-time. Per questo gli slot sono un numero
> **fisso (5)**, non uno-per-preset illimitato. È il workaround standard per
> "tile multiple configurabili".

Comportamento della tile principale: **toggle play/pausa dell'ultimo mix**.

> 📚 **Riferimento da studiare:** `Easy Noise` (FOSS, F-Droid) ha già una QS
> tile per suoni in loop. Leggere il suo `TileService` + come fa partire il
> service dalla tile a processo morto, prima di scrivere il nostro.
> Repo: cerca `com.cliambrown.easynoise` (GitHub/F-Droid).

Flusso al tap:
1. `onClick()` → invia un Intent al `PlaybackService`
   (`startForegroundService` con action `TOGGLE`).
2. Il Service controlla lo stato:
   - se sta suonando → pausa;
   - se è fermo → **carica l'ultimo mix da DataStore** e suona.
3. Il Service aggiorna lo stato → `onClick` aggiorna l'aspetto della Tile
   (`Tile.STATE_ACTIVE` / `STATE_INACTIVE`, icona, label).

**Il punto critico (cold start):** la Tile può essere premuta quando il
processo dell'app è morto e nessun player esiste. Per questo l'ultimo mix
**deve** essere persistito in DataStore e ricaricato dal Service all'avvio.
La persistenza che blankee ha già (preset) copre questo caso.

Casi limite da gestire:
- Primo avvio assoluto, nessun mix salvato → la Tile avvia un mix di default
  (es. solo pioggia) o apre l'app la prima volta.
- `onStartListening()` → sincronizza l'aspetto della Tile con lo stato reale
  quando il menu a tendina si apre.

### 3.5 Persistenza
DataStore (Preferences) con l'ultimo mix: lista di `(soundId, volume)` + flag
play/pausa. Niente Room finché non servono i preset multipli salvati (fase 3).

## 4. Permessi manifest
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" /> <!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" /> <!-- suoni custom, Fase 4 -->
```
- `<service android:foregroundServiceType="mediaPlayback">` per il PlaybackService.
- `<service android:permission="android.permission.BIND_QUICK_SETTINGS_TILE">`
  con `<intent-filter>` per `android.service.quicksettings.action.QS_TILE`
  per la Tile.
- `POST_NOTIFICATIONS` va richiesto a runtime su Android 13+.
- `minSdk 24` già copre la Quick Tile (API 24). OK.

## 5. UI (Compose)
- Griglia di card come Blanket: icona + nome + slider volume sotto ciascuna.
  **3 colonne** (4 in orizzontale/schermi larghi), scrollabile.
- Card attiva evidenziata (sfondo/icona colorata) quando volume > 0.
- Barra inferiore: volume master, play/pausa grande, menu.
- 3 temi: chiaro / scuro / sistema (default sistema).

### 5.1 Stile (direzione di lavoro, modificabile)
Ispirato allo stile di `apps/health`: **dark a strati** (sfondo ~`#121214`,
card `#161619`→`#1e1e22`), angoli arrotondati (10–16px), un solo accento.
- **Accento = gradiente viola→rosa**: `#9b8cff` → `#ff8fb1`. Mood "twilight".
- Gradiente solo su elementi grandi (play, card attiva, riempimento slider,
  tile selezionata). Su testo/icone piccole un tono **pieno** `#c69bff`
  (i gradienti sui dettagli piccoli peggiorano la leggibilità).

## 6. Audio assets e licenze
- I suoni di Blanket sono **CC0** (dominio pubblico) — riutilizzabili.
  Fonte: repo `rafaelmardojai/blanket` → cartella `data/.../sounds`.
- File in loop, formato `.ogg` (compatto, qualità buona). In `res/raw/` o `assets/`.
- Set MVP: pioggia, temporale, vento, onde, ruscello, uccelli, notte/grilli, treno.
- ⚠️ Verificare la licenza di ogni singolo file prima di includerlo.

## 7. Migrazione dal template attuale
Lo stato attuale è il template Compose con una "soundboard" che usa `SoundPool`.
- **Rimuovere `SoundPool`** da `MainActivity` (sbagliato per loop lunghi).
- Rinominare package `com.example.myapplication` → vedi §8.
- Tenere: struttura Compose, `Theme`/`Type`/`Color`, Gradle/Compose BOM.

## 8. Spostamento, rename, git — ✅ FATTO (commit 0d3def4)
- Spostato in `~/github/apps/sonari/`.
- `git init` + primo commit (identity personale).
- Package/namespace/applicationId → `io.github.liukscot.sonari`; tema →
  `SonariTheme` / `Theme.Sonari`; `app_name` + rootProject → "Sonari";
  cartelle sorgenti spostate in `io/github/liukscot/sonari/`.
- `.gitignore` esteso (build, .gradle, .kotlin, .claude-flow).

## 9. Roadmap a fasi
- **Fase 0 — setup**: ✅ FATTO — spostato in apps/, git init, rename package.
- **Fase 1 — MVP audio**: AudioEngine (Media3 ExoPlayer multi-loop, 15 suoni) +
  griglia UI con slider per suono + volume master + fade in/out + 3 temi
  (chiaro/scuro/sistema). Suona finché l'app è aperta. *(niente service/tile)*
- **Fase 2 — background + Quick Tile**: PlaybackService (Media3) + notifica +
  audio focus + persistenza ultimo mix (DataStore) + `TileService` con toggle
  dell'ultimo mix + cold start. Suona a schermo spento. **La feature richiesta.**
- **Fase 3 — sleep timer**: stop dopo N minuti (opzionale, default = infinito).
- **Fase 4 — suoni custom**: import file audio dell'utente (`READ_MEDIA_AUDIO` +
  UI aggiungi/rimuovi).
- **Fase 5 — preset**: salvare/caricare mix con nome (Room), come "afterRain".
- **Fase 6 — Quick Tile configurabile**: l'utente sceglie quale preset avvia la
  tile (eventualmente più tile per preset diversi). ← **il fossato vs concorrenza** (§2.1).

## 10. Decisioni prese
- [x] **Nome + package** → Sonari / `io.github.liukscot.sonari`.
- [x] **Posizionamento** → mixer di suoni di sottofondo (focus/studio/relax/sonno).
- [x] **Pubblicazione** → codice open su GitHub; F-Droid gratis; Play Store a
      pagamento (modello abbonamento vs una-tantum: *deciso in futuro*).
- [x] **Licenza** → GPLv3 (copyleft, come Blanket).
- [x] **Lingua UI** → inglese (per reach sugli store).
- [x] **Motore audio** → Media3 ExoPlayer.
- [x] **Set suoni** → tutti e 15 i suoni CC0 di Blanket al lancio.
- [x] **Suoni custom** → sì, supportati presto (Fase 4, dopo il timer).
- [x] **Sleep timer** → sì (Fase 3), opzionale, default = riproduzione infinita.
- [x] **Tema** → chiaro / scuro / sistema (default: sistema).
- [x] **Volume** → per-suono + master.
- [x] **Auto-resume al boot** → no.
- [x] **Fade in/out** → sì (~1s su play/pausa).
- [x] **minSdk** → 24 (Android 7).
- [x] **Layout griglia** → 3 colonne (4 in orizzontale).
- [x] **Stile UI** → dark a strati (stile `apps/health`), accento gradiente
      viola→rosa `#9b8cff`→`#ff8fb1` (pieno `#c69bff` sui dettagli).
- [x] **Preset** → l'utente salva i propri mix + 2-3 preset di default.
- [x] **Quick Tile** → 1 tile principale (toggle ultimo mix) + 5 slot-tile
      assegnabili a preset, spenti di default (vincolo: numero fisso).

### Da decidere più avanti
- [ ] Modello a pagamento Play Store (abbonamento vs costo una-tantum).
- [ ] Icona app / branding (ora è quella di default).
```
