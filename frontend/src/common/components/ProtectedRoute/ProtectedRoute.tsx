import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { PAGE_URL } from '../../constants/url';
import useAuthCheck from '../../hooks/useAuthCheck';
import LoadingSpinner from '../LoadingSpinner/LoadingSpinner';

function ProtectedRoute() {
  const location = useLocation();
  const { data, isError, isFetchedAfterMount, isFetching, isPending } =
    useAuthCheck();

  const checkingAuth = isPending || (isFetching && !isFetchedAfterMount);

  if (checkingAuth) {
    return <LoadingSpinner />;
  }

  if (isError || !data?.memberId) {
    return <Navigate to={PAGE_URL.LOGIN} replace state={{ from: location }} />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
