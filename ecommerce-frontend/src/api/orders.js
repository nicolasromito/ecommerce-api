import apiClient from './client';

export function createOrder(items) {
  const payload = {
    items: items.map((item) => ({
      productId: item.productId,
      quantity: item.quantity,
    })),
  };
  return apiClient.post('/orders', payload);
}

export function getMyOrders() {
  return apiClient.get('/orders');
}

export function getOrderById(id) {
  return apiClient.get(`/orders/${id}`);
}