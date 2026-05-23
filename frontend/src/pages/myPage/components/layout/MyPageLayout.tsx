import styled from '@emotion/styled';
import { Outlet } from 'react-router-dom';

import MyPageHeader from '../MyPageHeader/MyPageHeader';

function MyPageLayout() {
  return (
    <S_Container>
      <MyPageHeader />
      <Outlet />
    </S_Container>
  );
}

export default MyPageLayout;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
`;
