import { useCallback, useState } from 'react';

import {
  clearRecentSearchKeywords,
  getRecentSearchKeywords,
  removeRecentSearchKeyword,
  saveRecentSearchKeyword,
} from '../utils/recentSearchKeywords';

const useRecentSearchKeywords = () => {
  const [recentSearchKeywords, setRecentSearchKeywords] = useState<string[]>(
    () => getRecentSearchKeywords(),
  );

  const addRecentSearchKeyword = useCallback((keyword: string) => {
    setRecentSearchKeywords(saveRecentSearchKeyword(keyword));
  }, []);

  const deleteRecentSearchKeyword = useCallback((keyword: string) => {
    setRecentSearchKeywords(removeRecentSearchKeyword(keyword));
  }, []);

  const deleteAllRecentSearchKeywords = useCallback(() => {
    clearRecentSearchKeywords();
    setRecentSearchKeywords([]);
  }, []);

  return {
    recentSearchKeywords,
    addRecentSearchKeyword,
    deleteRecentSearchKeyword,
    deleteAllRecentSearchKeywords,
  };
};

export default useRecentSearchKeywords;
