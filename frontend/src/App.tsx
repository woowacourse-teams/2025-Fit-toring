import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import Booking from './pages/booking/Booking';
import { PAGE_URL } from './pages/common/constants/url';
import Detail from './pages/detail/Detail';
import Home from './pages/home/Home';

const router = createBrowserRouter([
  { path: PAGE_URL.HOME, element: <Home /> },
  { path: '/detail', element: <Detail /> }, // TODO: API 연결 시 /detail/{id} 와 같이 변경 필요
  { path: '/booking', element: <Booking /> },
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
