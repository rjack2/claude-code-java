import { getMovie } from '../api/movies.js';
import { getWatchlist, addToWatchlist, removeFromWatchlist } from '../api/users.js';
import { createSpinner } from '../components/spinner.js';
import { openPlayer } from '../components/player.js';

export async function renderMovie(app, id) {
  app.innerHTML = '';
  app.appendChild(createSpinner());

  let movie, watchlist;
  try {
    [movie, watchlist] = await Promise.all([
      getMovie(id),
      getWatchlist(),
    ]);
  } catch (err) {
    app.innerHTML = '';
    const msg = document.createElement('p');
    msg.className = 'page-error';
    msg.textContent = err.message;
    app.appendChild(msg);
    return;
  }

  app.innerHTML = '';

  const page = document.createElement('div');
  page.className = 'movie-page';

  // Backdrop
  const backdrop = document.createElement('div');
  backdrop.className = 'movie-backdrop';
  if (movie.backdropPath) {
    backdrop.style.backgroundImage = `url(https://image.tmdb.org/t/p/original${movie.backdropPath})`;
  } else {
    backdrop.style.backgroundColor = '#222';
  }

  const backdropOverlay = document.createElement('div');
  backdropOverlay.className = 'movie-backdrop-overlay';
  backdrop.appendChild(backdropOverlay);
  page.appendChild(backdrop);

  // Content
  const content = document.createElement('div');
  content.className = 'movie-content';

  const titleEl = document.createElement('h1');
  titleEl.className = 'movie-title';
  titleEl.textContent = movie.title;

  // Action buttons
  const actions = document.createElement('div');
  actions.className = 'movie-actions';

  const playBtn = document.createElement('button');
  playBtn.className = 'btn btn-play';
  playBtn.textContent = '▶ Play';
  playBtn.addEventListener('click', openPlayer);

  const inWatchlist = watchlist.includes(Number(id));
  const myListBtn = document.createElement('button');
  myListBtn.className = 'btn btn-mylist';
  myListBtn.textContent = inWatchlist ? '✓ My List' : '+ My List';

  let listed = inWatchlist;
  myListBtn.addEventListener('click', async () => {
    myListBtn.disabled = true;
    try {
      if (listed) {
        await removeFromWatchlist(id);
        listed = false;
        myListBtn.textContent = '+ My List';
      } else {
        await addToWatchlist(Number(id));
        listed = true;
        myListBtn.textContent = '✓ My List';
      }
    } catch (err) {
      // revert on error — button stays as-is
    } finally {
      myListBtn.disabled = false;
    }
  });

  actions.appendChild(playBtn);
  actions.appendChild(myListBtn);

  // Overview
  const overview = document.createElement('p');
  overview.className = 'movie-overview';
  overview.textContent = movie.overview;

  // Meta
  const meta = document.createElement('div');
  meta.className = 'movie-meta';

  function metaRow(label, value) {
    if (!value) return;
    const row = document.createElement('div');
    row.className = 'meta-row';
    const lbl = document.createElement('span');
    lbl.className = 'meta-label';
    lbl.textContent = label;
    const val = document.createElement('span');
    val.className = 'meta-value';
    val.textContent = value;
    row.appendChild(lbl);
    row.appendChild(val);
    meta.appendChild(row);
  }

  metaRow('Rating', movie.voteAverage != null ? `★ ${Number(movie.voteAverage).toFixed(1)} (${movie.voteCount} votes)` : null);
  metaRow('Release Date', movie.releaseDate);
  metaRow('Runtime', movie.runtime ? `${movie.runtime} min` : null);
  metaRow('Genres', movie.genres?.join(', '));
  metaRow('Themes', movie.themes?.join(', '));

  content.appendChild(titleEl);
  content.appendChild(actions);
  content.appendChild(overview);
  content.appendChild(meta);

  page.appendChild(content);

  app.appendChild(page);
}
