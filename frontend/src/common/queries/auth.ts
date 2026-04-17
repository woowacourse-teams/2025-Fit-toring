import { queryOptions } from '@tanstack/react-query';

import ApiError from '../apis/ApiError';
import { getAuthCheck } from '../apis/getAuthCheck';

export const authCheckQueryOptions = queryOptions({
  queryKey: ['authCheck'],
  queryFn: getAuthCheck,
  retry: (failureCount, error) => {
    const unAuthorized =
      error instanceof ApiError &&
      (error.status === 401 || error.status === 403);

    if (unAuthorized || failureCount >= 1) {
      return false;
    }
    return true;
  },
});
