import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import Home from './domain/mentoring/pages/Home';

const router = createBrowserRouter([{ path: '/', element: <Home /> }]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
