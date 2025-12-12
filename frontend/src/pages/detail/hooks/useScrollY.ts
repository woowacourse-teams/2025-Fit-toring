import { useState } from 'react';

const useScrollY = () => {
  const [scrollY, setScrollY] = useState(0);
  const changeScrollY = (y: number) => {
    setScrollY(y);
  };

  return { scrollY, changeScrollY };
};

export default useScrollY;
