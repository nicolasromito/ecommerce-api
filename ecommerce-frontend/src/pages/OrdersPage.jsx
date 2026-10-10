import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyOrders } from '../api/orders';

function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getMyOrders()
      .then((response) => setOrders(response.data))
      .catch(() => setError('No se pudieron cargar tus órdenes'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Cargando...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (orders.length === 0) return <p>Todavía no hiciste ninguna compra.</p>;

  return (
    <div>
      <h1>Mis órdenes</h1>
      {orders.map((order) => (
        <div key={order.id} style={{ border: '1px solid #ccc', padding: '1rem', marginBottom: '0.5rem' }}>
          <p>Orden #{order.id} — {order.status}</p>
          <p>Total: ${order.total}</p>
          <p>Fecha: {new Date(order.createdAt).toLocaleString()}</p>
          <Link to={`/orders/${order.id}`}>Ver detalle</Link>
        </div>
      ))}
    </div>
  );
}

export default OrdersPage;