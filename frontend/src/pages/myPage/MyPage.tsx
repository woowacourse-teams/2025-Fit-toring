import styled from '@emotion/styled';
import { Outlet } from 'react-router-dom';

import MyProfile from '../myPage/components/MyProfile/MyProfile';

import MyPageHeader from './components/MyPageHeader/MyPageHeader';

function MyPage() {
  return (
    <S_Container>
      <MyPageHeader />
      <MyProfile />
      <Outlet />
    </S_Container>
  );
}

export default MyPage;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
`;
