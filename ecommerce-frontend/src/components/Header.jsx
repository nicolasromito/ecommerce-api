import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

function Header() {
  const { user, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <header style={{ display: 'flex', gap: '1rem', padding: '1rem', borderBottom: '1px solid #ccc' }}>
      <Link to="/">Catálogo</Link>
      <Link to="/cart">Carrito ({itemCount})</Link>

      {user ? (
        <>
          <span>Hola, {user.username} ({user.role})</span>
          <button onClick={handleLogout}>Cerrar sesión</button>
        </>
      ) : (
        <>
          <Link to="/login">Iniciar sesión</Link>
          <Link to="/register">Registrarse</Link>
        </>
      )}
    </header>
  );
}

export default Header;