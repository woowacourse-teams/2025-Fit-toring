import { useEffect, useState } from 'react';

const useDelayedVisibility = (delay: number) => {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(true);
    }, delay);

    return () => clearTimeout(timer);
  }, [delay]);

  return visible;
};

export default useDelayedVisibility;
