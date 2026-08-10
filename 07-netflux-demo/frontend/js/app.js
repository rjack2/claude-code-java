import { renderNavbar } from './components/navbar.js';
import { renderLogin } from './pages/login.js';
import { renderRegister } from './pages/register.js';
import { renderHome } from './pages/home.js';
import { renderMovie } from './pages/movie.js';
import { renderGenre } from './pages/genre.js';
import { renderSearch } from './pages/search.js';

const PUBLIC_ROUTES = ['/login', '/register'];

function getPath() {
  return location.hash.slice(1) || '/login';
}

async function route() {
  const path = getPath();
  const token = localStorage.getItem('token');
  const isPublic = PUBLIC_ROUTES.some((r) => path.startsWith(r));

  if (!token && !isPublic) {
    location.hash = '#/login';
    return;
  }

  const app = document.getElementById('app');
  const navbarContainer = document.getElementById('navbar-container');

  if (token && !isPublic) {
    renderNavbar(navbarContainer);
  } else {
    navbarContainer.innerHTML = '';
  }

  if (path === '/login') return renderLogin(app);
  if (path === '/register') return renderRegister(app);
  if (path === '/home') return renderHome(app);

  const movieMatch = path.match(/^\/movie\/(.+)$/);
  if (movieMatch) return renderMovie(app, movieMatch[1]);

  const genreMatch = path.match(/^\/genre\/(.+)$/);
  if (genreMatch) return renderGenre(app, decodeURIComponent(genreMatch[1]));

  const searchMatch = path.match(/^\/search\/(.+)$/);
  if (searchMatch) return renderSearch(app, decodeURIComponent(searchMatch[1]));

  location.hash = '#/login';
}

window.addEventListener('hashchange', route);
window.addEventListener('load', route);
