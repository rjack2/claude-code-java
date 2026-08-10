import { register } from '../api/users.js';

export function renderRegister(app) {
  app.innerHTML = '';

  const container = document.createElement('div');
  container.className = 'auth-container';

  const form = document.createElement('form');
  form.className = 'auth-form';

  const heading = document.createElement('h1');
  heading.textContent = 'Create Account';

  const nameInput = document.createElement('input');
  nameInput.type = 'text';
  nameInput.placeholder = 'Full Name';
  nameInput.required = true;

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
  submitBtn.textContent = 'Register';

  const loginLink = document.createElement('p');
  loginLink.className = 'auth-link';
  const a = document.createElement('a');
  a.href = '#/login';
  a.textContent = 'Already have an account? Sign in.';
  loginLink.appendChild(a);

  form.appendChild(heading);
  form.appendChild(nameInput);
  form.appendChild(usernameInput);
  form.appendChild(passwordInput);
  form.appendChild(errorMsg);
  form.appendChild(submitBtn);
  form.appendChild(loginLink);
  container.appendChild(form);
  app.appendChild(container);

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    errorMsg.textContent = '';
    submitBtn.disabled = true;
    try {
      await register(usernameInput.value, passwordInput.value, nameInput.value);
      location.hash = '#/login';
    } catch (err) {
      errorMsg.textContent = err.message;
      submitBtn.disabled = false;
    }
  });
}
