const VIDEO_URL = 'https://www.youtube.com/embed/bNJW113tbKk?autoplay=1&controls=0&modestbranding=1&rel=0';

export function openPlayer() {
  const overlay = document.createElement('div');
  overlay.className = 'player-overlay';

  const dialog = document.createElement('div');
  dialog.className = 'player-dialog';

  const closeBtn = document.createElement('button');
  closeBtn.className = 'player-close';
  closeBtn.textContent = '✕';

  const iframe = document.createElement('iframe');
  iframe.className = 'player-iframe';
  iframe.src = VIDEO_URL;
  iframe.allow = 'autoplay; encrypted-media';
  iframe.allowFullscreen = true;

  dialog.appendChild(closeBtn);
  dialog.appendChild(iframe);
  overlay.appendChild(dialog);
  document.body.appendChild(overlay);

  function close() { overlay.remove(); }

  closeBtn.addEventListener('click', close);
  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  document.addEventListener('keydown', function onEsc(e) {
    if (e.key === 'Escape') { close(); document.removeEventListener('keydown', onEsc); }
  });
}
