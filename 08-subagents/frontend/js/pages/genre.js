import { searchByGenre } from '../api/movies.js';
import { createMovieCard } from '../components/movieCard.js';
import { createSpinner } from '../components/spinner.js';

export async function renderGenre(app, genre) {
  app.innerHTML = '';
  app.appendChild(createSpinner());

  let movies;
  try {
    movies = await searchByGenre(genre);
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
  page.className = 'browse-page';

  const heading = document.createElement('h1');
  heading.className = 'browse-heading';
  heading.textContent = genre;

  const grid = document.createElement('div');
  grid.className = 'movie-grid';

  for (const movie of movies) {
    grid.appendChild(createMovieCard(movie));
  }

  page.appendChild(heading);
  page.appendChild(grid);
  app.appendChild(page);
}
