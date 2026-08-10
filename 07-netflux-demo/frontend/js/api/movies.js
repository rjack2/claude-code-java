import { get } from './http.js';

export const getMovie = (id) => get(`/api/movies/${id}`);

export const searchByTitle = (query, limit = 100) =>
  get(`/api/movies/search?title=${encodeURIComponent(query)}&limit=${limit}`);

export const searchByGenre = (genre, limit = 100) =>
  get(`/api/movies/search?genre=${encodeURIComponent(genre)}&limit=${limit}`);
