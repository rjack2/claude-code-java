async function http(method, url, body) {
  const headers = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('token');
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));

    if (res.status === 401 && token) {
      localStorage.clear();
      location.hash = '#/login';
      throw new Error(err.detail || 'Session expired');
    }

    throw new Error(err.detail || 'An error occurred');
  }

  if (res.status === 204) return null;
  return res.json().catch(() => null);
}

export const get = (url) => http('GET', url);
export const post = (url, body) => http('POST', url, body);
export const del = (url) => http('DELETE', url);
