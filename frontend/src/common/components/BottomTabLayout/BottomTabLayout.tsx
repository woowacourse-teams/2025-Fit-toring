import { Outlet } from 'react-router-dom';

import BottomNavigationBar from '../BottomNavigationBar/BottomNavigationBar';

function BottomTabLayout() {
  return (
    <>
      <Outlet />
      <BottomNavigationBar />
    </>
  );
}

export default BottomTabLayout;
