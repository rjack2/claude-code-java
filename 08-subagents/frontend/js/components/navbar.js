export function renderNavbar(container) {
  if (container.querySelector('.navbar')) return;

  const nav = document.createElement('nav');
  nav.className = 'navbar';

  const left = document.createElement('div');
  left.className = 'navbar-left';

  const logo = document.createElement('span');
  logo.className = 'navbar-logo';
  logo.textContent = 'NETFLUX';
  logo.addEventListener('click', () => { location.hash = '#/home'; });

  const homeLink = document.createElement('a');
  homeLink.className = 'navbar-link';
  homeLink.textContent = 'Home';
  homeLink.href = '#/home';

  left.appendChild(logo);
  left.appendChild(homeLink);

  const right = document.createElement('div');
  right.className = 'navbar-right';

  const searchWrapper = document.createElement('div');
  searchWrapper.className = 'search-wrapper';

  const searchIcon = document.createElement('button');
  searchIcon.className = 'search-icon-btn';
  searchIcon.setAttribute('aria-label', 'Search');
  searchIcon.textContent = '⌕';

  const searchInput = document.createElement('input');
  searchInput.type = 'text';
  searchInput.className = 'search-input';
  searchInput.placeholder = 'Titles';

  searchIcon.addEventListener('click', () => {
    searchWrapper.classList.toggle('search-expanded');
    if (searchWrapper.classList.contains('search-expanded')) searchInput.focus();
  });

  searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      const q = searchInput.value.trim();
      if (q) {
        searchWrapper.classList.remove('search-expanded');
        searchInput.value = '';
        location.hash = `#/search/${encodeURIComponent(q)}`;
      }
    } else if (e.key === 'Escape') {
      searchWrapper.classList.remove('search-expanded');
      searchInput.value = '';
    }
  });

  searchWrapper.appendChild(searchIcon);
  searchWrapper.appendChild(searchInput);

  const storedName = localStorage.getItem('name') || '?';
  const initials = storedName
    .split(' ')
    .map((w) => w[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  const avatar = document.createElement('div');
  avatar.className = 'navbar-avatar';
  avatar.textContent = initials;

  const signOut = document.createElement('button');
  signOut.className = 'navbar-signout';
  signOut.textContent = 'Sign Out';
  signOut.addEventListener('click', () => {
    localStorage.clear();
    container.innerHTML = '';
    location.hash = '#/login';
  });

  right.appendChild(searchWrapper);
  right.appendChild(avatar);
  right.appendChild(signOut);

  nav.appendChild(left);
  nav.appendChild(right);
  container.appendChild(nav);

  window.addEventListener('scroll', () => {
    nav.classList.toggle('navbar--solid', window.scrollY >= 50);
  });
}
