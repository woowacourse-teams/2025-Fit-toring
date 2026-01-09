import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';

import { getAuthCheck } from '../apis/getAuthCheck';
import { useAuth } from '../components/AuthProvider/AuthProvider';

export const AUTH_CHECK_QUERY_KEY = ['authCheck'] as const;

const useAuthCheck = () => {
  const { login, logout } = useAuth();

  const { data, isSuccess, isError } = useQuery({
    queryKey: AUTH_CHECK_QUERY_KEY,
    queryFn: getAuthCheck,
    retry: false,
  });

  useEffect(() => {
    if (isError) {
      logout();
      localStorage.removeItem('memberId');
      return;
    }

    if (!isSuccess) {
      return;
    }

    const memberId = data.memberId;
    if (!memberId) {
      logout();
      localStorage.removeItem('memberId');
      return;
    }

    login();
    localStorage.setItem('memberId', memberId.toString());
  }, [data?.memberId, isError, isSuccess, login, logout]);
};

export default useAuthCheck;
