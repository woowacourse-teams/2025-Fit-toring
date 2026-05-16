export type InstallPromptPlatform = 'ios' | 'android';

const INSTALL_PROMPT_SHOW_STORAGE_KEY_BY_PLATFORM: Record<
  InstallPromptPlatform,
  string
> = {
  ios: 'pwa_install_prompt_shown_ios',
  android: 'pwa_install_prompt_shown_android',
};

const canUseLocalStorage = (): boolean => {
  if (typeof window === 'undefined') {
    return false;
  }

  try {
    const testKey = '__pwa_install_prompt_storage_test__';
    window.localStorage.setItem(testKey, '1');
    window.localStorage.removeItem(testKey);
    return true;
  } catch {
    return false;
  }
};

const readFlag = (key: string): boolean => {
  if (!canUseLocalStorage()) {
    return false;
  }

  const raw = window.localStorage.getItem(key);
  return raw === 'true';
};

const writeFlag = (key: string, value: boolean): void => {
  if (!canUseLocalStorage()) {
    return;
  }

  window.localStorage.setItem(key, String(value));
};

export const getInstallPromptShown = (
  platform: InstallPromptPlatform,
): boolean => readFlag(INSTALL_PROMPT_SHOW_STORAGE_KEY_BY_PLATFORM[platform]);

export const markInstallPromptShown = (
  platform: InstallPromptPlatform,
  shown = true,
): void => {
  writeFlag(INSTALL_PROMPT_SHOW_STORAGE_KEY_BY_PLATFORM[platform], shown);
};

export const shouldAutoShowInstallPromptOnHome = ({
  isStandalone,
  platform,
}: {
  isStandalone: boolean;
  platform: InstallPromptPlatform;
}): boolean => {
  if (isStandalone) {
    return false;
  }

  return !getInstallPromptShown(platform);
};
