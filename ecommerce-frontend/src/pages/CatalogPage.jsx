import { useEffect, useState } from 'react';
import { getProducts, getCategories } from '../api/products';
import ProductCard from '../components/ProductCard';

function CatalogPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [categoryId, setCategoryId] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getCategories()
      .then((response) => setCategories(response.data))
      .catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    setLoading(true);
    getProducts({ page, categoryId, minPrice, maxPrice })
      .then((response) => {
        setProducts(response.data.content);
        setTotalPages(response.data.totalPages);
      })
      .finally(() => setLoading(false));
  }, [page, categoryId, minPrice, maxPrice]);

  function handleFilterChange(setter) {
    return (e) => {
      setter(e.target.value);
      setPage(0);
    };
  }

  return (
    <div>
      <h1>Catálogo</h1>

      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
        <select value={categoryId} onChange={handleFilterChange(setCategoryId)}>
          <option value="">Todas las categorías</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>

        <input
          type="number"
          placeholder="Precio mínimo"
          value={minPrice}
          onChange={handleFilterChange(setMinPrice)}
        />
        <input
          type="number"
          placeholder="Precio máximo"
          value={maxPrice}
          onChange={handleFilterChange(setMaxPrice)}
        />
      </div>

      {loading ? (
        <p>Cargando...</p>
      ) : (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '1rem' }}>
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          <div style={{ marginTop: '1rem' }}>
            <button disabled={page === 0} onClick={() => setPage(page - 1)}>Anterior</button>
            <span style={{ margin: '0 1rem' }}>Página {page + 1} de {totalPages}</span>
            <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>Siguiente</button>
          </div>
        </>
      )}
    </div>
  );
}

export default CatalogPage;