import type { PropsWithChildren } from 'react';
import React, { createContext, useContext, useEffect, useState } from 'react';

import { postReissue } from '../../apis/postReissue';

interface AuthContextValue {
  authenticated: boolean;
  setAuthenticated: (value: boolean) => void;
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
      }
    };

    checkAuth();
  }, []);

  return (
    <AuthContext.Provider value={{ authenticated, setAuthenticated }}>
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
