// Sonari sample data — the 15 built-in CC0 sounds (from Blanket) + presets.
window.SONARI_SOUNDS = [
  { id: 'rain',    name: 'Rain',        icon: 'cloud-rain' },
  { id: 'storm',   name: 'Storm',       icon: 'cloud-lightning' },
  { id: 'wind',    name: 'Wind',        icon: 'wind' },
  { id: 'waves',   name: 'Waves',       icon: 'waves' },
  { id: 'stream',  name: 'Stream',      icon: 'droplet' },
  { id: 'birds',   name: 'Birds',       icon: 'bird' },
  { id: 'night',   name: 'Summer night',icon: 'moon-star' },
  { id: 'crickets',name: 'Crickets',    icon: 'bug' },
  { id: 'train',   name: 'Train',       icon: 'train-front' },
  { id: 'boat',    name: 'Boat',        icon: 'sailboat' },
  { id: 'city',    name: 'City',        icon: 'building-2' },
  { id: 'coffee',  name: 'Coffee shop', icon: 'coffee' },
  { id: 'fire',    name: 'Fireplace',   icon: 'flame' },
  { id: 'white',   name: 'White noise', icon: 'audio-waveform' },
  { id: 'pink',    name: 'Pink noise',  icon: 'radio' },
];

window.SONARI_PRESETS = [
  { id: 'afterrain', name: 'afterRain', icon: 'cloud-drizzle', tile: true,
    mix: { rain: 70, stream: 38, birds: 24 } },
  { id: 'focus',     name: 'Deep focus', icon: 'brain', tile: true,
    mix: { rain: 55, coffee: 48, white: 30 } },
  { id: 'sleep',     name: 'Night sleep', icon: 'moon', tile: false,
    mix: { wind: 40, night: 52, crickets: 30 } },
  { id: 'voyage',    name: 'Voyage', icon: 'sailboat', tile: false,
    mix: { waves: 66, boat: 44, wind: 28 } },
];
