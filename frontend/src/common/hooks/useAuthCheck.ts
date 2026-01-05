import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';

import { getAuthCheck } from '../apis/authCheck';
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
    }

    if (isSuccess) {
      if (data.memberId) {
        localStorage.setItem('memberId', data.memberId.toString());
        login();
      } else {
        logout();
      }
    }
  }, [data?.memberId, isError, isSuccess, login, logout]);
};

export default useAuthCheck;
