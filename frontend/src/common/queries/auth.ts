import { queryOptions } from '@tanstack/react-query';

import { getAuthCheck } from '../apis/getAuthCheck';

export const authCheckQueryOptions = queryOptions({
  queryKey: ['authCheck'],
  queryFn: getAuthCheck,
  retry: false,
});
