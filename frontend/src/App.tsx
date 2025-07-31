import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from './common/constants/url';
import Booking from './pages/booking/Booking';
import Detail from './pages/detail/Detail';
import Home from './pages/home/Home';
import Signup from './pages/signup/Signup';

const router = createBrowserRouter([
  { path: PAGE_URL.HOME, element: <Home /> },
  { path: `${PAGE_URL.DETAIL}/:mentoringId`, element: <Detail /> },
  { path: `${PAGE_URL.BOOKING}/:mentoringId`, element: <Booking /> },
  { path: PAGE_URL.SIGNUP, element: <Signup /> },
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
