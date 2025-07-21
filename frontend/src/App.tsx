import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';

import { PAGE_URL } from './domain/common/constants/url';
import Detail from './domain/mentoring/pages/Detail';
import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([
  { path: PAGE_URL.HOME, element: <Home /> },
  { path: '/detail', element: <Detail /> }, // TODO: API 연결 시 /detail/{id} 와 같이 변경 필요
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
