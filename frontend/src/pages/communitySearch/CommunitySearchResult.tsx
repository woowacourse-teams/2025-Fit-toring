import { useEffect } from 'react';

import styled from '@emotion/styled';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { PAGE_URL } from '../../common/constants/url';

import CommunitySearchHeader from './components/CommunitySearchHeader/CommunitySearchHeader';
import CommunitySearchResultContent from './components/CommunitySearchResultContent/CommunitySearchResultContent';
import useRecentSearchKeywords from './hooks/useRecentSearchKeywords';

function CommunitySearchResult() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { addRecentSearchKeyword } = useRecentSearchKeywords();
  const keyword = searchParams.get('keyword')?.trim() ?? '';

  useEffect(() => {
    if (!keyword) {
      navigate(PAGE_URL.COMMUNITY_SEARCH, { replace: true });
    }
  }, [keyword, navigate]);

  if (!keyword) {
    return null;
  }

  return (
    <S_Container>
      <CommunitySearchHeader
        defaultKeyword={keyword}
        onSearch={addRecentSearchKeyword}
        redirectToSearchOnEmpty
      />
      <CommunitySearchResultContent keyword={keyword} />
    </S_Container>
  );
}

export default CommunitySearchResult;

const S_Container = styled.div`
  min-height: 100dvh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
