import { lazy } from 'react';

import {
  createBrowserRouter,
  redirect,
  RouterProvider,
} from 'react-router-dom';

import BottomTabLayout from './common/components/BottomTabLayout/BottomTabLayout';
import MobileLayout from './common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from './common/constants/url';
import ChatRooms from './pages/chatRooms/ChatRooms';
import Home from './pages/home/Home';
import Landing from './pages/landing/Landing';

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
const Booking = lazy(() => import('./pages/booking/Booking'));
const CreatedMentoring = lazy(
  () => import('./pages/createdMentoring/CreatedMentoring'),
);
const EditProfile = lazy(() => import('./pages/editProfile/EditProfile'));
const ChatRoom = lazy(() => import('./pages/chatRoom/ChatRoom'));
const IdentityVerification = lazy(
  () => import('./pages/identityVerification/IdentityVerification'),
);

const router = createBrowserRouter([
  {
    element: <MobileLayout />,
    children: [
      {
        element: <BottomTabLayout />,
        children: [
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
          { path: `${PAGE_URL.CHAT_ROOMS}`, element: <ChatRooms /> },
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
              {
                path: PAGE_URL.EDIT_PROFILE,
                element: <EditProfile />,
              },
            ],
          },
        ],
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
        path: `${PAGE_URL.CHAT_ROOM}/:chatRoomId`,
        element: <ChatRoom />,
      },
      {
        path: PAGE_URL.IDENTITY_VERIFICATION,
        element: <IdentityVerification />,
      },
    ],
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
