import { beforeEach, describe, expect, it } from 'vitest';

import {
  clearRecentSearchKeywords,
  getRecentSearchKeywords,
  RECENT_SEARCH_KEYWORD_MAX_COUNT,
  removeRecentSearchKeyword,
  saveRecentSearchKeyword,
} from '../src/pages/communitySearch/utils/recentSearchKeywords';

describe('community recent search keywords', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('stores trimmed keyword as newest search', () => {
    saveRecentSearchKeyword('  면접  ');

    expect(getRecentSearchKeywords()).toEqual(['면접']);
  });

  it('moves duplicate keyword to newest position', () => {
    saveRecentSearchKeyword('자바');
    saveRecentSearchKeyword('프론트엔드');
    saveRecentSearchKeyword('자바');

    expect(getRecentSearchKeywords()).toEqual(['자바', '프론트엔드']);
  });

  it('keeps up to 9 keywords and removes oldest first', () => {
    Array.from({ length: RECENT_SEARCH_KEYWORD_MAX_COUNT + 1 }, (_, index) =>
      saveRecentSearchKeyword(`검색어${index + 1}`),
    );

    expect(getRecentSearchKeywords()).toEqual([
      '검색어10',
      '검색어9',
      '검색어8',
      '검색어7',
      '검색어6',
      '검색어5',
      '검색어4',
      '검색어3',
      '검색어2',
    ]);
  });

  it('removes selected keyword', () => {
    saveRecentSearchKeyword('자바');
    saveRecentSearchKeyword('프론트엔드');

    removeRecentSearchKeyword('자바');

    expect(getRecentSearchKeywords()).toEqual(['프론트엔드']);
  });

  it('clears all keywords', () => {
    saveRecentSearchKeyword('자바');
    saveRecentSearchKeyword('프론트엔드');

    clearRecentSearchKeywords();

    expect(getRecentSearchKeywords()).toEqual([]);
  });
});
