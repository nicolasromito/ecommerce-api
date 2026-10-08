import { useEffect, useState } from 'react';
import apiClient from './api/client';

function App() {
  const [products, setProducts] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient.get('/products')
      .then((response) => setProducts(response.data))
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Test de conexión</h1>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {products && <pre>{JSON.stringify(products, null, 2)}</pre>}
    </div>
  );
}

export default App;