import { login } from '../api/users.js';

export function renderLogin(app) {
  app.innerHTML = '';

  const container = document.createElement('div');
  container.className = 'auth-container';

  const form = document.createElement('form');
  form.className = 'auth-form';

  const heading = document.createElement('h1');
  heading.textContent = 'Sign In';

  const usernameInput = document.createElement('input');
  usernameInput.type = 'text';
  usernameInput.placeholder = 'Username';
  usernameInput.required = true;

  const passwordInput = document.createElement('input');
  passwordInput.type = 'password';
  passwordInput.placeholder = 'Password';
  passwordInput.required = true;

  const errorMsg = document.createElement('p');
  errorMsg.className = 'auth-error';

  const submitBtn = document.createElement('button');
  submitBtn.type = 'submit';
  submitBtn.textContent = 'Sign In';

  const registerLink = document.createElement('p');
  registerLink.className = 'auth-link';
  const a = document.createElement('a');
  a.href = '#/register';
  a.textContent = 'New to Netflux? Register now.';
  registerLink.appendChild(a);

  form.appendChild(heading);
  form.appendChild(usernameInput);
  form.appendChild(passwordInput);
  form.appendChild(errorMsg);
  form.appendChild(submitBtn);
  form.appendChild(registerLink);
  container.appendChild(form);
  app.appendChild(container);

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    errorMsg.textContent = '';
    submitBtn.disabled = true;
    try {
      const { token, name } = await login(usernameInput.value, passwordInput.value);
      localStorage.setItem('token', token);
      localStorage.setItem('name', name);
      location.hash = '#/home';
    } catch (err) {
      errorMsg.textContent = err.message;
      submitBtn.disabled = false;
    }
  });
}
