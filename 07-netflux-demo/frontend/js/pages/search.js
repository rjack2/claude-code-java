import { searchByTitle } from '../api/movies.js';
import { createMovieCard } from '../components/movieCard.js';
import { createSpinner } from '../components/spinner.js';

export async function renderSearch(app, query) {
  app.innerHTML = '';
  app.appendChild(createSpinner());

  let movies;
  try {
    movies = await searchByTitle(query);
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
  heading.textContent = `Results for "${query}"`;

  page.appendChild(heading);

  if (movies.length === 0) {
    const empty = document.createElement('p');
    empty.className = 'empty-state';
    empty.textContent = 'No titles match your search. Try something else.';
    page.appendChild(empty);
  } else {
    const grid = document.createElement('div');
    grid.className = 'movie-grid';
    for (const movie of movies) {
      grid.appendChild(createMovieCard(movie));
    }
    page.appendChild(grid);
  }

  app.appendChild(page);
}
