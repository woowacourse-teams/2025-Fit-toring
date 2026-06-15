import styled from '@emotion/styled';

import closeBlackIcon from '../../../../common/assets/images/closeBlack.svg';

interface RecentSearchSectionProps {
  keywords: readonly string[];
  onKeywordClick: (keyword: string) => void;
  onKeywordRemove: (keyword: string) => void;
  onClear: () => void;
}

function RecentSearchSection({
  keywords,
  onKeywordClick,
  onKeywordRemove,
  onClear,
}: RecentSearchSectionProps) {
  const hasKeywords = keywords.length > 0;
  const handleClearButtonClick = () => {
    if (!window.confirm('최근 검색어를 모두 삭제하시겠습니까?')) {
      return;
    }

    onClear();
  };

  return (
    <S_Section aria-labelledby="recent-search-title">
      <S_Header>
        <S_Title id="recent-search-title">최근 검색어</S_Title>
        {hasKeywords && (
          <S_ClearButton type="button" onClick={handleClearButtonClick}>
            전체삭제
          </S_ClearButton>
        )}
      </S_Header>
      {hasKeywords ? (
        <S_List>
          {keywords.map((keyword) => (
            <S_Item key={keyword}>
              <S_KeywordButton
                type="button"
                onClick={() => onKeywordClick(keyword)}
              >
                {keyword}
              </S_KeywordButton>
              <S_RemoveButton
                type="button"
                aria-label={`${keyword} 검색어 삭제`}
                onClick={() => onKeywordRemove(keyword)}
              >
                <S_RemoveIcon src={closeBlackIcon} alt="" aria-hidden="true" />
              </S_RemoveButton>
            </S_Item>
          ))}
        </S_List>
      ) : (
        <S_EmptyText>최근 검색어가 없습니다.</S_EmptyText>
      )}
    </S_Section>
  );
}

export default RecentSearchSection;

const S_Section = styled.section`
  padding: 3.2rem 2rem 0;
`;

const S_Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_B};
`;

const S_ClearButton = styled.button`
  padding: 0;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  ${({ theme }) => theme.TYPOGRAPHY.B4_SB};
  cursor: pointer;
`;

const S_List = styled.ul`
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem 1rem;

  margin-top: 2rem;
`;

const S_Item = styled.li`
  display: flex;
  align-items: center;
  gap: 0.8rem;

  height: 4.2rem;
  max-width: 100%;
  padding: 0 1.4rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 2.1rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_KeywordButton = styled.button`
  overflow: hidden;

  max-width: 20rem;
  padding: 0;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  text-overflow: ellipsis;

  white-space: nowrap;
  cursor: pointer;
`;

const S_RemoveButton = styled.button`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;

  width: 2rem;
  height: 2rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_RemoveIcon = styled.img`
  width: 1.2rem;
  height: 1.2rem;

  opacity: 0.68;
`;

const S_EmptyText = styled.p`
  padding-top: 9.6rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  text-align: center;
`;
