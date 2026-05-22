import styled from '@emotion/styled';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { PAGE_URL } from '../../constants/url';
import useAuthCheck from '../../hooks/useAuthCheck';
import useDelayedVisibility from '../../hooks/useDelayedVisibility';
import LoadingSpinner from '../LoadingSpinner/LoadingSpinner';

const LOADING_SPINNER_DELAY_MS = 1000;

function ProtectedRoute() {
  const location = useLocation();
  const showLoadingSpinner = useDelayedVisibility(LOADING_SPINNER_DELAY_MS);
  const { data, isError, isFetchedAfterMount, isFetching, isPending } =
    useAuthCheck();

  const checkingAuth = isPending || (isFetching && !isFetchedAfterMount);

  if (checkingAuth) {
    return showLoadingSpinner ? (
      <S_LoadingContainer>
        <LoadingSpinner />
      </S_LoadingContainer>
    ) : null;
  }

  if (isError || !data?.memberId) {
    return <Navigate to={PAGE_URL.LOGIN} replace state={{ from: location }} />;
  }

  return <Outlet />;
}

export default ProtectedRoute;

const S_LoadingContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: 100dvh;
`;
