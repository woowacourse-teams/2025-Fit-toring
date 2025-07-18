import { useEffect } from 'react';

const useEscapeKeyDown = (onEscape: () => void, enabled: boolean) => {
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if (enabled && e.key === 'Escape') {
        onEscape();
      }
    }

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [enabled, onEscape]);
};

export default useEscapeKeyDown;
