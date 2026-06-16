export const RECENT_SEARCH_KEYWORD_MAX_COUNT = 9;

const RECENT_SEARCH_KEYWORDS_STORAGE_KEY = 'communityRecentSearchKeywords';

const isStorageAccessError = (error: unknown) => {
  if (error instanceof Error) {
    return true;
  }

  return (
    typeof DOMException !== 'undefined' && error instanceof DOMException
  );
};

const canUseLocalStorage = () => {
  if (typeof window === 'undefined') {
    return false;
  }

  const testKey = `${RECENT_SEARCH_KEYWORDS_STORAGE_KEY}:test`;

  try {
    window.localStorage.setItem(testKey, '1');
    window.localStorage.removeItem(testKey);
    return true;
  } catch (error) {
    if (isStorageAccessError(error)) {
      return false;
    }

    throw error;
  }
};

const isStringArray = (value: unknown): value is readonly string[] => {
  return Array.isArray(value) && value.every((item) => typeof item === 'string');
};

const normalizeRecentSearchKeywords = (
  keywords: readonly string[],
): string[] => {
  const normalizedKeywords: string[] = [];

  keywords.forEach((keyword) => {
    const trimmedKeyword = keyword.trim();

    if (
      !trimmedKeyword ||
      normalizedKeywords.includes(trimmedKeyword) ||
      normalizedKeywords.length >= RECENT_SEARCH_KEYWORD_MAX_COUNT
    ) {
      return;
    }

    normalizedKeywords.push(trimmedKeyword);
  });

  return normalizedKeywords;
};

const setRecentSearchKeywords = (keywords: readonly string[]) => {
  if (!canUseLocalStorage()) {
    return;
  }

  window.localStorage.setItem(
    RECENT_SEARCH_KEYWORDS_STORAGE_KEY,
    JSON.stringify(keywords),
  );
};

export const getRecentSearchKeywords = () => {
  if (!canUseLocalStorage()) {
    return [];
  }

  const rawKeywords = window.localStorage.getItem(
    RECENT_SEARCH_KEYWORDS_STORAGE_KEY,
  );

  if (!rawKeywords) {
    return [];
  }

  try {
    const parsedKeywords: unknown = JSON.parse(rawKeywords);

    if (!isStringArray(parsedKeywords)) {
      return [];
    }

    return normalizeRecentSearchKeywords(parsedKeywords);
  } catch (error) {
    if (error instanceof SyntaxError) {
      return [];
    }

    throw error;
  }
};

export const saveRecentSearchKeyword = (keyword: string) => {
  const trimmedKeyword = keyword.trim();
  const currentKeywords = getRecentSearchKeywords();

  if (!trimmedKeyword) {
    return currentKeywords;
  }

  const nextKeywords = normalizeRecentSearchKeywords([
    trimmedKeyword,
    ...currentKeywords.filter(
      (currentKeyword) => currentKeyword !== trimmedKeyword,
    ),
  ]);

  setRecentSearchKeywords(nextKeywords);

  return nextKeywords;
};

export const removeRecentSearchKeyword = (keyword: string) => {
  const nextKeywords = getRecentSearchKeywords().filter(
    (currentKeyword) => currentKeyword !== keyword,
  );

  setRecentSearchKeywords(nextKeywords);

  return nextKeywords;
};

export const clearRecentSearchKeywords = () => {
  if (!canUseLocalStorage()) {
    return;
  }

  window.localStorage.removeItem(RECENT_SEARCH_KEYWORDS_STORAGE_KEY);
};
