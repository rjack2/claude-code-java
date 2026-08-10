import { getHome } from '../api/recommendations.js';
import { createMovieCard } from '../components/movieCard.js';
import { createSpinner } from '../components/spinner.js';
import { openPlayer } from '../components/player.js';

export async function renderHome(app) {
  app.innerHTML = '';
  app.appendChild(createSpinner());

  let data;
  try {
    data = await getHome();
  } catch (err) {
    app.innerHTML = '';
    const msg = document.createElement('p');
    msg.className = 'page-error';
    msg.textContent = err.message;
    app.appendChild(msg);
    return;
  }

  app.innerHTML = '';

  renderHero(app, data.popular);

  const content = document.createElement('div');
  content.className = 'home-content';

  if (data.watchlist && data.watchlist.length > 0) {
    content.appendChild(renderRow('My List', data.watchlist));
  }

  content.appendChild(renderRow('Popular', data.popular));
  content.appendChild(renderRow('New Releases', data.latest));

  for (const [genre, movies] of Object.entries(data.byGenre)) {
    content.appendChild(renderRow(genre, movies, `#/genre/${encodeURIComponent(genre)}`));
  }

  app.appendChild(content);
}

function renderHero(app, popular) {
  if (!popular || popular.length === 0) return;

  const section = document.createElement('div');
  section.className = 'hero';

  const bg = document.createElement('div');
  bg.className = 'hero-bg';

  const overlay = document.createElement('div');
  overlay.className = 'hero-overlay';

  const info = document.createElement('div');
  info.className = 'hero-info';

  const title = document.createElement('h1');
  title.className = 'hero-title';

  const rating = document.createElement('p');
  rating.className = 'hero-rating';

  const buttons = document.createElement('div');
  buttons.className = 'hero-buttons';

  const playBtn = document.createElement('button');
  playBtn.className = 'btn btn-play';
  playBtn.textContent = '▶ Play';
  playBtn.addEventListener('click', openPlayer);

  const infoBtn = document.createElement('button');
  infoBtn.className = 'btn btn-info';
  infoBtn.textContent = 'ⓘ More Info';

  buttons.appendChild(playBtn);
  buttons.appendChild(infoBtn);
  info.appendChild(title);
  info.appendChild(rating);
  info.appendChild(buttons);
  overlay.appendChild(info);
  section.appendChild(bg);
  section.appendChild(overlay);
  app.appendChild(section);

  let currentIndex = null;

  function showMovie(movie) {
    section.classList.add('hero-fade');
    setTimeout(() => {
      if (movie.backdropPath) {
        bg.style.backgroundImage = `url(https://image.tmdb.org/t/p/original${movie.backdropPath})`;
      } else {
        bg.style.backgroundImage = 'none';
        bg.style.backgroundColor = '#222';
      }
      title.textContent = movie.title;
      rating.textContent = `★ ${movie.voteAverage != null ? Number(movie.voteAverage).toFixed(1) : 'N/A'}`;
      infoBtn.onclick = () => { location.hash = `#/movie/${movie.id}`; };
      section.classList.remove('hero-fade');
    }, 400);
  }

  function pickNext() {
    let idx;
    do {
      idx = Math.floor(Math.random() * popular.length);
    } while (popular.length > 1 && idx === currentIndex);
    currentIndex = idx;
    showMovie(popular[idx]);
  }

  pickNext();
  const interval = setInterval(pickNext, 5000);

  // Stop rotating when navigating away
  window.addEventListener('hashchange', () => clearInterval(interval), { once: true });
}

function renderRow(title, movies, viewAllHref) {
  const section = document.createElement('div');
  section.className = 'row-section';

  const header = document.createElement('div');
  header.className = 'row-header';

  const label = document.createElement('h2');
  label.className = 'row-title';
  label.textContent = title;
  header.appendChild(label);

  if (viewAllHref) {
    const viewAll = document.createElement('a');
    viewAll.className = 'row-view-all';
    viewAll.href = viewAllHref;
    viewAll.textContent = 'View All';
    header.appendChild(viewAll);
  }

  section.appendChild(header);

  const wrapper = document.createElement('div');
  wrapper.className = 'row-wrapper';

  const leftArrow = document.createElement('button');
  leftArrow.className = 'row-arrow row-arrow--left';
  leftArrow.textContent = '‹';
  leftArrow.style.display = 'none';

  const track = document.createElement('div');
  track.className = 'row-track';

  for (const movie of movies) {
    track.appendChild(createMovieCard(movie));
  }

  const rightArrow = document.createElement('button');
  rightArrow.className = 'row-arrow row-arrow--right';
  rightArrow.textContent = '›';

  wrapper.appendChild(leftArrow);
  wrapper.appendChild(track);
  wrapper.appendChild(rightArrow);
  section.appendChild(wrapper);

  function updateArrows() {
    leftArrow.style.display = track.scrollLeft <= 0 ? 'none' : 'flex';
    const atEnd = track.scrollLeft + track.clientWidth >= track.scrollWidth - 1;
    rightArrow.style.display = atEnd ? 'none' : 'flex';
  }

  leftArrow.addEventListener('click', () => {
    track.scrollBy({ left: -600, behavior: 'smooth' });
  });

  rightArrow.addEventListener('click', () => {
    track.scrollBy({ left: 600, behavior: 'smooth' });
  });

  track.addEventListener('scroll', updateArrows);

  // Update arrows after images load
  requestAnimationFrame(updateArrows);

  return section;
}
