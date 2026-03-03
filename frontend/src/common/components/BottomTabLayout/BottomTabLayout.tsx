import styled from '@emotion/styled';
import { Outlet } from 'react-router-dom';

import BottomNavigationBar from '../BottomNavigationBar/BottomNavigationBar';

function BottomTabLayout() {
  return (
    <>
      <S_BottomTabContents>
        <Outlet />
      </S_BottomTabContents>
      <BottomNavigationBar />
    </>
  );
}

export default BottomTabLayout;

const S_BottomTabContents = styled.div`
  padding-bottom: 72px;
`;
