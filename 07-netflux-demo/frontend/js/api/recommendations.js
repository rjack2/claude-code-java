import { get } from './http.js';

export const getHome = () => get('/api/recommendations/home');
