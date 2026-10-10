import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { createOrder } from '../api/orders';

function CartPage() {
  const { items, removeItem, updateQuantity, clearCart, total } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  async function handleCheckout() {
    if (!user) {
      navigate('/login');
      return;
    }

    setError(null);
    setLoading(true);

    try {
      await createOrder(items);
      clearCart();
      navigate('/orders');
    } catch (err) {
      const message = err.response?.data?.message || 'No se pudo completar la compra';
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  if (items.length === 0) {
    return <p>Tu carrito está vacío.</p>;
  }

  return (
    <div>
      <h1>Carrito</h1>

      {items.map((item) => (
        <div key={item.productId} style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '0.5rem' }}>
          <span style={{ flex: 1 }}>{item.name}</span>
          <input
            type="number"
            min="1"
            value={item.quantity}
            onChange={(e) => updateQuantity(item.productId, parseInt(e.target.value, 10))}
            style={{ width: '60px' }}
          />
          <span>${item.price * item.quantity}</span>
          <button onClick={() => removeItem(item.productId)}>Quitar</button>
        </div>
      ))}

      <h2>Total: ${total}</h2>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={handleCheckout} disabled={loading}>
        {loading ? 'Procesando...' : 'Confirmar compra'}
      </button>
    </div>
  );
}

export default CartPage;