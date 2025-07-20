import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import MobileLayout from './common/components/MobileLayout/MobileLayout';
import MentoringBooking from './domain/booking/pages/MentoringBooking';
import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([
  { path: '/', element: <Home /> },
  { path: '/booking', element: <MentoringBooking /> },
]);

function App() {
  return (
    <MobileLayout>
      <RouterProvider router={router} />
    </MobileLayout>
  );
}

export default App;
