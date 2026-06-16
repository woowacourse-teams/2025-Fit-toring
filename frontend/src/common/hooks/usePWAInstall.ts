import { createContext, useContext } from 'react';

export interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>;
  userChoice: Promise<{
    outcome: 'accepted' | 'dismissed';
    platform: string;
  }>;
}

export interface UsePWAInstallResult {
  canInstall: boolean;
  hasInstalledBefore: boolean;
  promptInstall: () => Promise<void>;
  resetInstallPrompt: () => void;
}

export const PWAInstallContext = createContext<UsePWAInstallResult>({
  canInstall: false,
  hasInstalledBefore: false,
  promptInstall: async () => {},
  resetInstallPrompt: () => {},
});

const usePWAInstall = (): UsePWAInstallResult => {
  return useContext(PWAInstallContext);
};

export default usePWAInstall;
