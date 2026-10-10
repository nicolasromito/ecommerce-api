import { createContext, useContext, useState } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  async function login(username, password) {
    const response = await apiLogin(username, password);
    const { token, username: respUsername, role } = response.data;

    localStorage.setItem('token', token);
    const userData = { username: respUsername, role };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);

    return userData;
  }

  async function register(username, email, password) {
    const response = await apiRegister(username, email, password);
    const { token, username: respUsername, role } = response.data;

    localStorage.setItem('token', token);
    const userData = { username: respUsername, role };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);

    return userData;
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }

  const value = { user, login, register, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}