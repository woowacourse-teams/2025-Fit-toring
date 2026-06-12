import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../common/constants/url';

import CommunitySearchHeader from './components/CommunitySearchHeader/CommunitySearchHeader';
import RecentSearchSection from './components/RecentSearchSection/RecentSearchSection';
import useRecentSearchKeywords from './hooks/useRecentSearchKeywords';

function CommunitySearch() {
  const navigate = useNavigate();
  const {
    recentSearchKeywords,
    addRecentSearchKeyword,
    deleteRecentSearchKeyword,
    deleteAllRecentSearchKeywords,
  } = useRecentSearchKeywords();

  const handleRecentSearchKeywordClick = (keyword: string) => {
    addRecentSearchKeyword(keyword);
    navigate(
      `${PAGE_URL.COMMUNITY_SEARCH_RESULT}?keyword=${encodeURIComponent(
        keyword,
      )}`,
    );
  };

  return (
    <S_Container>
      <CommunitySearchHeader
        autoFocus
        onSearch={addRecentSearchKeyword}
      />
      <RecentSearchSection
        keywords={recentSearchKeywords}
        onKeywordClick={handleRecentSearchKeywordClick}
        onKeywordRemove={deleteRecentSearchKeyword}
        onClear={deleteAllRecentSearchKeywords}
      />
    </S_Container>
  );
}

export default CommunitySearch;

const S_Container = styled.div`
  min-height: 100dvh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
