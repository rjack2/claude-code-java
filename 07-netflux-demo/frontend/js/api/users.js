import { get, post, del } from './http.js';

export const login = (username, password) =>
  post('/api/users/login', { username, password });

export const register = (username, password, name) =>
  post('/api/users/register', { username, password, name });

export const getWatchlist = () => get('/api/users/watchlist');

export const addToWatchlist = (movieId) =>
  post('/api/users/watchlist', { movieId });

export const removeFromWatchlist = (movieId) =>
  del(`/api/users/watchlist/${movieId}`);
