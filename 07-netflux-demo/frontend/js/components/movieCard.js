export function createMovieCard(movie) {
  const card = document.createElement('div');
  card.className = 'movie-card';

  if (movie.posterPath) {
    const img = document.createElement('img');
    img.src = `https://image.tmdb.org/t/p/w500${movie.posterPath}`;
    img.alt = movie.title;
    card.appendChild(img);
  } else {
    const placeholder = document.createElement('div');
    placeholder.className = 'card-placeholder';
    const text = document.createElement('span');
    text.textContent = movie.title;
    placeholder.appendChild(text);
    card.appendChild(placeholder);
  }

  const overlay = document.createElement('div');
  overlay.className = 'card-overlay';

  const title = document.createElement('span');
  title.className = 'card-title';
  title.textContent = movie.title;

  const rating = document.createElement('span');
  rating.className = 'card-rating';
  rating.textContent = `★ ${movie.voteAverage != null ? Number(movie.voteAverage).toFixed(1) : 'N/A'}`;

  overlay.appendChild(title);
  overlay.appendChild(rating);
  card.appendChild(overlay);

  card.addEventListener('click', () => {
    location.hash = `#/movie/${movie.id}`;
  });

  return card;
}
