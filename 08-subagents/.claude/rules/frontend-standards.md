---
description: Frontend coding standards
paths:
  - "**/*.js"
  - "**/*.html"
  - "**/*.css"
---

# Frontend Standards

## File Structure

```
src/
    index.html
    css/
        styles.css
    js/
        app.js           : router and bootstrap
        api/             : one file per upstream service
        pages/           : one file per page
        components/      : reusable UI components
```

## JavaScript

- Use ES6 modules (`import`/`export`) only:
  - No `var`
  - No IIFE wrappers
- Use `const` by default. Use `let` only when reassignment is needed.
- One file per page under `js/pages/`. One file per upstream service under `js/api/`.
- All fetch calls go through `js/api/`.
- No inline `fetch` calls in page files.

## DOM Manipulation

- Never use `innerHTML` with data from the server or user input.
- Use `textContent` or `createElement` instead.
- Build repeated elements with a dedicated render function, not inline string concatenation.

Example:
```js
// Wrong
container.innerHTML = `<div class="card">${item.title}</div>`;

// Correct
const card = document.createElement('div');
card.className = 'card';
card.textContent = item.title;
container.appendChild(card);
```

## CSS

- Single stylesheet: `css/styles.css`.
- No inline styles in JavaScript.
