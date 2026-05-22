import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';

import { useAuth } from '../components/AuthProvider/AuthProvider';
import { authCheckQueryOptions } from '../queries/auth';

const useAuthCheck = () => {
  const { login, logout } = useAuth();

  const authCheckQuery = useQuery(authCheckQueryOptions);
  const { data, isSuccess, isError } = authCheckQuery;

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

  return authCheckQuery;
};

export default useAuthCheck;
