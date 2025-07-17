import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout';
import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([{ path: '/', element: <Home /> }]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
