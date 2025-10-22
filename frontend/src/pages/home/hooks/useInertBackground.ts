import { useEffect } from 'react';

const useInertBackground = (opened: boolean) => {
  useEffect(() => {
    if (!opened) {
      return;
    }

    const appRoot = document.getElementById('root');
    if (appRoot) {
      appRoot.setAttribute('aria-hidden', 'true');
      appRoot.setAttribute('inert', '');
    }

    return () => {
      if (appRoot) {
        appRoot.removeAttribute('aria-hidden');
        appRoot.removeAttribute('inert');
      }
    };
  }, [opened]);
};

export default useInertBackground;
