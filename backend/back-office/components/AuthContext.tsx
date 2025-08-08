import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { isLoggedIn, getStoredUser, deleteCookie } from './LoginPage';

interface User {
  id: string;
  username: string;
  name: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (userData: User) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 페이지 로드 시 기존 로그인 상태 확인
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        console.log('🔍 Checking login status...');
        const hasToken = isLoggedIn();
        console.log('🔑 Has access token:', hasToken);
        
        if (hasToken) {
          const userData = getStoredUser();
          console.log('👤 Stored user data:', userData);
          
          if (userData) {
            console.log('✅ User authenticated, setting user data');
            setUser(userData);
          } else {
            console.log('❌ No user data found, logging out');
            // 토큰은 있지만 사용자 데이터가 없는 경우 로그아웃 처리
            handleLogout();
          }
        } else {
          console.log('❌ No access token found');
        }
      } catch (error) {
        console.error('❌ Error checking login status:', error);
        // 오류 발생 시 로그아웃 처리
        handleLogout();
      } finally {
        console.log('✅ Login status check complete');
        setIsLoading(false);
      }
    };

    checkLoginStatus();
  }, []);

  const handleLogin = (userData: User) => {
    console.log('🎉 User logged in:', userData);
    setUser(userData);
  };

  const handleLogout = () => {
    console.log('🚪 Logging out user');
    // 모든 쿠키 삭제
    deleteCookie('accessToken');
    deleteCookie('refreshToken');
    deleteCookie('userData');
    
    setUser(null);
    console.log('✅ User logged out');
  };

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login: handleLogin,
    logout: handleLogout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}