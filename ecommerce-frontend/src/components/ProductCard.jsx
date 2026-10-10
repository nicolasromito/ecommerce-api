import { useCart } from '../context/CartContext';

function ProductCard({ product }) {
  const { addItem } = useCart();

  return (
    <div style={{ border: '1px solid #ccc', padding: '1rem', borderRadius: '8px' }}>
      <h3>{product.name}</h3>
      <p>{product.description}</p>
      <p><strong>${product.price}</strong></p>
      <p>Categoría: {product.categoryName}</p>
      <p>Stock: {product.stock}</p>
      <button onClick={() => addItem(product)} disabled={product.stock === 0}>
        Agregar al carrito
      </button>
    </div>
  );
}

export default ProductCard;