import { lazy } from 'react';

import {
  createBrowserRouter,
  redirect,
  RouterProvider,
} from 'react-router-dom';

import BottomTabLayout from './common/components/BottomTabLayout/BottomTabLayout';
import ChannelTalkProvider from './common/components/ChannelTalkProvider/ChannelTalkProvider';
import MobileLayout from './common/components/MobileLayout/MobileLayout';
import ProtectedRoute from './common/components/ProtectedRoute/ProtectedRoute';
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
const MyPageLayout = lazy(
  () => import('./pages/myPage/components/layout/MyPageLayout'),
);
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
const CommunityPostDetail = lazy(
  () => import('./pages/communityPostDetail/CommunityPostDetail'),
);
const IdentityVerification = lazy(
  () => import('./pages/identityVerification/IdentityVerification'),
);
const Community = lazy(() => import('./pages/community/Community'));
const CommunityPostCreate = lazy(
  () => import('./pages/communityPostCreate/CommunityPostCreate'),
);
const CommunityPostUpdate = lazy(
  () => import('./pages/communityPostUpdate/CommunityPostUpdate'),
);

const router = createBrowserRouter([
  {
    element: (
      <>
        <MobileLayout />
      </>
    ),
    children: [
      {
        element: <BottomTabLayout />,
        children: [
          {
            path: PAGE_URL.HOME,
            element: (
              <ChannelTalkProvider>
                <Home />
              </ChannelTalkProvider>
            ),
            loader: () => {
              const firstVisited = !sessionStorage.getItem('hasVisited');

              if (firstVisited) {
                return redirect(PAGE_URL.LANDING);
              }
              return null;
            },
          },
          {
            element: <ProtectedRoute />,
            children: [
              { path: `${PAGE_URL.CHAT_ROOMS}`, element: <ChatRooms /> },
              {
                path: `${PAGE_URL.MY_PAGE}`,
                element: <MyPageLayout />,
                children: [
                  {
                    index: true,
                    element: <MyPage />,
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
          {
            path: PAGE_URL.COMMUNITY,
            element: <Community />,
          },
          {
            path: `${PAGE_URL.COMMUNITY}/:postId`,
            element: <CommunityPostDetail />,
          },
          { path: PAGE_URL.LOGIN, element: <Login /> },
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
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: `${PAGE_URL.CHAT_ROOM}/:chatRoomId`,
            element: <ChatRoom />,
          },
        ],
      },
      {
        path: PAGE_URL.IDENTITY_VERIFICATION,
        element: <IdentityVerification />,
      },
      {
        path: PAGE_URL.COMMUNITY_CREATE,
        element: <CommunityPostCreate />,
      },
      {
        path: `${PAGE_URL.COMMUNITY}/:postId${PAGE_URL.EDIT}`,
        element: <CommunityPostUpdate />,
      },
    ],
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
