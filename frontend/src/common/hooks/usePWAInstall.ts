import { useCallback, useEffect, useState } from 'react';

export interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>;
  userChoice: Promise<{
    outcome: 'accepted' | 'dismissed';
    platform: string;
  }>;
}

interface UsePWAInstallResult {
  canInstall: boolean;
  promptInstall: () => Promise<void>;
  resetInstallPrompt: () => void;
}

const usePWAInstall = (): UsePWAInstallResult => {
  const [installPromptEvent, setInstallPromptEvent] =
    useState<BeforeInstallPromptEvent | null>(null);

  const resetInstallPrompt = useCallback(() => {
    setInstallPromptEvent(null);
  }, []);

  const promptInstall = useCallback(async (): Promise<void> => {
    if (!installPromptEvent) {
      return;
    }

    try {
      await installPromptEvent.prompt();
      await installPromptEvent.userChoice;
      resetInstallPrompt();
    } catch (error) {
      console.error('[PWA] 설치 프롬프트 실행 실패:', error);
      resetInstallPrompt();
    }
  }, [installPromptEvent, resetInstallPrompt]);

  useEffect(() => {
    const handleBeforeInstallPrompt = (event: Event) => {
      const beforeInstallPromptEvent =
        event as BeforeInstallPromptEvent;

      event.preventDefault();
      setInstallPromptEvent(beforeInstallPromptEvent);
    };

    const handleAppInstalled = () => {
      resetInstallPrompt();
    };

    window.addEventListener(
      'beforeinstallprompt',
      handleBeforeInstallPrompt,
    );
    window.addEventListener('appinstalled', handleAppInstalled);

    return () => {
      window.removeEventListener(
        'beforeinstallprompt',
        handleBeforeInstallPrompt,
      );
      window.removeEventListener('appinstalled', handleAppInstalled);
    };
  }, [resetInstallPrompt]);

  return {
    canInstall: installPromptEvent !== null,
    promptInstall,
    resetInstallPrompt,
  };
};

export default usePWAInstall;
