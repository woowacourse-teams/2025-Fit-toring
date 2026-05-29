import { queryOptions } from '@tanstack/react-query';

import ApiError from '../apis/ApiError';
import { getAuthCheck } from '../apis/getAuthCheck';

export const authCheckQueryOptions = queryOptions({
  queryKey: ['authCheck'],
  queryFn: getAuthCheck,
  retry: (failureCount, error) => {
    if (error instanceof ApiError) {
      return error.status >= 500 && failureCount < 1;
    }

    return failureCount < 1;
  },
});
