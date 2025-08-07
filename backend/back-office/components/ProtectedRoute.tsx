import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  console.log('🛡️ ProtectedRoute - isLoading:', isLoading, 'isAuthenticated:', isAuthenticated);

  // 로딩 중일 때는 로딩 화면 표시
  if (isLoading) {
    console.log('⏳ ProtectedRoute - Still loading, showing loading screen');
    return (
      <div className="min-h-screen flex items-center justify-center bg-muted/30">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">로딩 중...</p>
        </div>
      </div>
    );
  }

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  if (!isAuthenticated) {
    console.log('❌ ProtectedRoute - Not authenticated, redirecting to login');
    // 현재 위치를 state로 저장하여 로그인 후 원래 페이지로 돌아갈 수 있게 함
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  console.log('✅ ProtectedRoute - Authenticated, rendering protected content');
  return <>{children}</>;
}