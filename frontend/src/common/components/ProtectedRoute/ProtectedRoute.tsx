import { Navigate, Outlet, useLocation } from 'react-router-dom';

import useDelayedVisibility from '../../../pages/chatRoom/hooks/useDelayedVisibility';
import { PAGE_URL } from '../../constants/url';
import useAuthCheck from '../../hooks/useAuthCheck';
import LoadingSpinner from '../LoadingSpinner/LoadingSpinner';

const LOADING_SPINNER_DELAY_MS = 1000;

function ProtectedRoute() {
  const location = useLocation();
  const showLoadingSpinner = useDelayedVisibility(LOADING_SPINNER_DELAY_MS);
  const { data, isError, isFetchedAfterMount, isFetching, isPending } =
    useAuthCheck();

  const checkingAuth = isPending || (isFetching && !isFetchedAfterMount);

  if (checkingAuth) {
    return showLoadingSpinner ? <LoadingSpinner /> : null;
  }

  if (isError || !data?.memberId) {
    return <Navigate to={PAGE_URL.LOGIN} replace state={{ from: location }} />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
