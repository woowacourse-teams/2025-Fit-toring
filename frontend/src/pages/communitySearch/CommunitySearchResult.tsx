import { useEffect } from 'react';

import styled from '@emotion/styled';
import { useNavigate, useSearchParams } from 'react-router-dom';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import CommunitySearchHeader from './components/CommunitySearchHeader/CommunitySearchHeader';
import CommunitySearchResultContent from './components/CommunitySearchResultContent/CommunitySearchResultContent';
import useRecentSearchKeywords from './hooks/useRecentSearchKeywords';

function CommunitySearchResult() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { addRecentSearchKeyword } = useRecentSearchKeywords();
  const keyword = searchParams.get('keyword')?.trim() ?? '';
  const canonicalSearch = new URLSearchParams();
  if (keyword) {
    canonicalSearch.set('keyword', keyword);
  }
  const query = canonicalSearch.toString();
  const canonicalPath = query
    ? `${PAGE_URL.COMMUNITY_SEARCH_RESULT}?${query}`
    : PAGE_URL.COMMUNITY_SEARCH_RESULT;

  const seo = (
    <SEO
      title="커뮤니티 검색 결과 - 핏토링 Fittoring"
      canonicalPath={canonicalPath}
    />
  );

  const navigateToSearchPage = () => {
    navigate(PAGE_URL.COMMUNITY_SEARCH, { replace: true });
  };

  useEffect(() => {
    if (!keyword) {
      navigate(PAGE_URL.COMMUNITY_SEARCH, { replace: true });
    }
  }, [keyword, navigate]);

  if (!keyword) {
    return seo;
  }

  return (
    <S_Container>
      {seo}
      <CommunitySearchHeader
        defaultKeyword={keyword}
        onSearch={addRecentSearchKeyword}
        onSearchReset={navigateToSearchPage}
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
