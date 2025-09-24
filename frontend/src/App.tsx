import {
  createBrowserRouter,
  redirect,
  RouterProvider,
} from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from './common/constants/url';
import Landing from './pages/landing/Landing';
import { lazy } from 'react';

const Login = lazy(() => import('./pages/login/Login'));
const MentoringCreate = lazy(
  () => import('./pages/mentoringCreate/MentoringCreate'),
);
const MentoringUpdate = lazy(
  () => import('./pages/mentoringUpdate/MentoringUpdate'),
);
const MyPage = lazy(() => import('./pages/myPage/MyPage'));
const ParticipatedMentoring = lazy(
  () => import('./pages/participatedMentoring/ParticipatedMentoring'),
);
const Signup = lazy(() => import('./pages/signup/Signup'));
const Detail = lazy(() => import('./pages/detail/Detail'));
const Home = lazy(() => import('./pages/home/Home'));
const Booking = lazy(() => import('./pages/booking/Booking'));
const CreatedMentoring = lazy(
  () => import('./pages/createdMentoring/CreatedMentoring'),
);

const router = createBrowserRouter([
  {
    path: PAGE_URL.HOME,
    element: <Home />,
    loader: () => {
      const firstVisited = !sessionStorage.getItem('hasVisited');

      if (firstVisited) {
        return redirect(PAGE_URL.LANDING);
      }
      return null;
    },
  },
  { path: PAGE_URL.LANDING, element: <Landing /> },
  { path: `${PAGE_URL.DETAIL}/:mentoringId`, element: <Detail /> },
  { path: `${PAGE_URL.BOOKING}/:mentoringId`, element: <Booking /> },
  { path: PAGE_URL.SIGNUP, element: <Signup /> },
  { path: PAGE_URL.MENTORING_CREATE, element: <MentoringCreate /> },
  {
    path: `${PAGE_URL.MENTORING_UPDATE}/:mentoringId`,
    element: <MentoringUpdate />,
  },
  { path: PAGE_URL.LOGIN, element: <Login /> },
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
      {
        path: PAGE_URL.PARTICIPATED_MENTORING,
        element: <ParticipatedMentoring />,
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
