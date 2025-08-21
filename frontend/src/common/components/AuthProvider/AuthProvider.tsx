import type { PropsWithChildren } from 'react';
import React, { createContext, useContext, useEffect, useState } from 'react';

import { postReissue } from '../../apis/postReissue';
import { captureSentryError } from '../../utils/captureSentryError';

interface AuthContextValue {
  authenticated: boolean;
  login: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function AuthProvider({ children }: PropsWithChildren) {
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await postReissue();
        setAuthenticated(true);
      } catch (error) {
        console.error(error);
        setAuthenticated(false);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'auth',
          step: 'auth-check',
        });
      }
    };

    checkAuth();
  }, []);

  const login = () => {
    setAuthenticated(true);
  };

  const logout = () => {
    setAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ authenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export default AuthProvider;

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
