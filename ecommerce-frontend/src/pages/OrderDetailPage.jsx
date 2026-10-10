import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getOrderById } from '../api/orders';

function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getOrderById(id)
      .then((response) => setOrder(response.data))
      .catch((err) => {
        const message = err.response?.status === 403
          ? 'No tenés permiso para ver esta orden'
          : 'No se pudo cargar la orden';
        setError(message);
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <p>Cargando...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div>
      <Link to="/orders">&larr; Volver a mis órdenes</Link>
      <h1>Orden #{order.id}</h1>
      <p>Estado: {order.status}</p>
      <p>Fecha: {new Date(order.createdAt).toLocaleString()}</p>

      {order.items.map((item) => (
        <div key={item.productId} style={{ display: 'flex', gap: '1rem' }}>
          <span>{item.productName}</span>
          <span>x{item.quantity}</span>
          <span>${item.subtotal}</span>
        </div>
      ))}

      <h2>Total: ${order.total}</h2>
    </div>
  );
}

export default OrderDetailPage;