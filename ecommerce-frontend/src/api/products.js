import apiClient from './client';

export function getProducts({ page = 0, size = 10, categoryId, minPrice, maxPrice } = {}) {
  const params = { page, size };
  if (categoryId) params.categoryId = categoryId;
  if (minPrice) params.minPrice = minPrice;
  if (maxPrice) params.maxPrice = maxPrice;

  return apiClient.get('/products', { params });
}

export function getCategories() {
  return apiClient.get('/categories');
}