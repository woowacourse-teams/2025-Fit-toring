export const INSTALL_PROMPT_SHOW_STORAGE_KEY = 'pwa_install_prompt_shown';

const canUseLocalStorage = (): boolean => {
  return (
    typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'
  );
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

export const getInstallPromptShown = (): boolean =>
  readFlag(INSTALL_PROMPT_SHOW_STORAGE_KEY);

export const markInstallPromptShown = (shown = true): void => {
  writeFlag(INSTALL_PROMPT_SHOW_STORAGE_KEY, shown);
};

export const shouldAutoShowInstallPromptOnLoginHome = ({
  isStandalone,
  shown,
}: {
  isStandalone: boolean;
  shown?: boolean;
}): boolean => {
  if (isStandalone) {
    return false;
  }

  if (shown) {
    return false;
  }

  return true;
};
