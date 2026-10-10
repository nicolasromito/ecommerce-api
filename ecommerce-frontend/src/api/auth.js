import apiClient from './client';

export function login(username, password) {
  return apiClient.post('/auth/login', { username, password });
}

export function register(username, email, password) {
  return apiClient.post('/auth/register', { username, email, password });
}