import type { PropsWithChildren } from 'react';
import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { PWAInstallContext } from '../../hooks/usePWAInstall';
import { captureSentryError } from '../../utils/captureSentryError';

import type {
  BeforeInstallPromptEvent,
  UsePWAInstallResult,
} from '../../hooks/usePWAInstall';

const PWA_INSTALLED_STORAGE_KEY = 'pwa_installed';

function getHasInstalledBefore(): boolean {
  try {
    return localStorage.getItem(PWA_INSTALLED_STORAGE_KEY) === 'true';
  } catch {
    return false;
  }
}

function markInstalledBefore() {
  try {
    localStorage.setItem(PWA_INSTALLED_STORAGE_KEY, 'true');
  } catch {
    return;
  }
}

function PWAInstallProvider({ children }: PropsWithChildren) {
  const [installPromptEvent, setInstallPromptEvent] =
    useState<BeforeInstallPromptEvent | null>(null);
  const [hasInstalledBefore, setHasInstalledBefore] = useState(
    getHasInstalledBefore,
  );

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
      captureSentryError({
        error,
        level: 'warning',
        feature: 'pwa',
        step: 'install-prompt-execute',
      });
      resetInstallPrompt();
    }
  }, [installPromptEvent, resetInstallPrompt]);

  useEffect(() => {
    const handleBeforeInstallPrompt = (event: Event) => {
      const beforeInstallPromptEvent = event as BeforeInstallPromptEvent;

      event.preventDefault();
      setInstallPromptEvent(beforeInstallPromptEvent);
    };

    const handleAppInstalled = () => {
      markInstalledBefore();
      setHasInstalledBefore(true);
      resetInstallPrompt();
    };

    window.addEventListener('beforeinstallprompt', handleBeforeInstallPrompt);
    window.addEventListener('appinstalled', handleAppInstalled);

    return () => {
      window.removeEventListener(
        'beforeinstallprompt',
        handleBeforeInstallPrompt,
      );
      window.removeEventListener('appinstalled', handleAppInstalled);
    };
  }, [resetInstallPrompt]);

  const value = useMemo<UsePWAInstallResult>(
    () => ({
      canInstall: installPromptEvent !== null,
      hasInstalledBefore,
      promptInstall,
      resetInstallPrompt,
    }),
    [hasInstalledBefore, installPromptEvent, promptInstall, resetInstallPrompt],
  );

  return (
    <PWAInstallContext.Provider value={value}>
      {children}
    </PWAInstallContext.Provider>
  );
}

export default PWAInstallProvider;
