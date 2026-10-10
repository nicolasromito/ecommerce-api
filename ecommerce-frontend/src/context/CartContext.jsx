import { createContext, useContext, useState } from 'react';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [items, setItems] = useState([]);

  function addItem(product, quantity = 1) {
    setItems((prevItems) => {
      const existing = prevItems.find((item) => item.productId === product.id);

      if (existing) {
        return prevItems.map((item) =>
          item.productId === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        );
      }

      return [...prevItems, {
        productId: product.id,
        name: product.name,
        price: product.price,
        quantity,
      }];
    });
  }

  function removeItem(productId) {
    setItems((prevItems) => prevItems.filter((item) => item.productId !== productId));
  }

  function updateQuantity(productId, quantity) {
    if (quantity < 1) return;
    setItems((prevItems) =>
      prevItems.map((item) =>
        item.productId === productId ? { ...item, quantity } : item
      )
    );
  }

  function clearCart() {
    setItems([]);
  }

  const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);

  const value = { items, addItem, removeItem, updateQuantity, clearCart, total, itemCount };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  return useContext(CartContext);
}