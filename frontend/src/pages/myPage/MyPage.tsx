import styled from '@emotion/styled';
import { Outlet } from 'react-router-dom';

import MyProfile from '../myPage/components/MyProfile/MyProfile';

import MyPageHeader from './components/MyPageHeader/MyPageHeader';

function MyPage() {
  return (
    <StyledContainer>
      <MyPageHeader />
      <MyProfile />
      <Outlet />
    </StyledContainer>
  );
}

export default MyPage;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
`;
