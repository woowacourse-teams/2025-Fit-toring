import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import Detail from './domain/mentoring/pages/Detail';
import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([
  // TODO: 데모데이 전 path 상수화
  { path: '/', element: <Home /> },
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
