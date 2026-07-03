import styled from '@emotion/styled';

import CommunityContent from '../../../community/components/CommunityContent/CommunityContent';

interface CommunitySearchResultContentProps {
  keyword: string;
}

function CommunitySearchResultContent({
  keyword,
}: CommunitySearchResultContentProps) {
  return (
    <>
      <S_ResultSummary>
        <S_Keyword>{keyword}</S_Keyword> 검색 결과
      </S_ResultSummary>
      <CommunityContent
        keyword={keyword}
        emptyMessage="검색 결과가 없습니다."
      />
    </>
  );
}

export default CommunitySearchResultContent;

const S_ResultSummary = styled.p`
  padding: 1.8rem 1.6rem 0.8rem;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_Keyword = styled.strong`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_SB};
`;
