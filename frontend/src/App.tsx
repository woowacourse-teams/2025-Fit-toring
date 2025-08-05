import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from './common/constants/url';
import Booking from './pages/booking/Booking';
import CreatedMentoring from './pages/CreatedMentoring/CreatedMentoring';
import Detail from './pages/detail/Detail';
import Home from './pages/home/Home';
import Signup from './pages/signup/Signup';
import MentoringCreate from './pages/mentoringCreate/MentoringCreate';
import MyPage from './pages/myPage/MyPage';

const router = createBrowserRouter([
  { path: PAGE_URL.HOME, element: <Home /> },
  { path: `${PAGE_URL.DETAIL}/:mentoringId`, element: <Detail /> },
  { path: `${PAGE_URL.BOOKING}/:mentoringId`, element: <Booking /> },
  { path: PAGE_URL.SIGNUP, element: <Signup /> },
  { path: PAGE_URL.MENTORING_CREATE, element: <MentoringCreate /> },
  {
    path: `${PAGE_URL.MY_PAGE}`,
    element: <MyPage />,
    children: [
      {
        index: true,
        element: <CreatedMentoring />,
      },
      {
        path: PAGE_URL.CREATED_MENTORING,
        element: <CreatedMentoring />,
      },
    ],
  }, // TODO: `${PAGE_URL.MY_PAGE}/:userId`로 변경 예정
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
