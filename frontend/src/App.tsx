import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from './domain/common/constants/url';
import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([
  { path: PAGE_URL.HOME, element: <Home /> },
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
