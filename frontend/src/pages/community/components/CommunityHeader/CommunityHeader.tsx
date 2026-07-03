import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import searchIcon from '../../../../common/assets/images/searchIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';

function CommunityHeader() {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  const handleSearchButtonClick = () => {
    navigate(PAGE_URL.COMMUNITY_SEARCH);
  };

  return (
    <Header>
      <S_Wrapper>
        <S_BackButton type="button" onClick={handleBackButtonClick}>
          <S_BackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </S_BackButton>
        <S_Title>커뮤니티</S_Title>
        <S_SearchButton
          type="button"
          aria-label="커뮤니티 게시글 검색"
          onClick={handleSearchButtonClick}
        >
          <S_SearchIcon src={searchIcon} alt="" aria-hidden="true" />
        </S_SearchButton>
      </S_Wrapper>
    </Header>
  );
}

export default CommunityHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const S_BackButton = styled.button`
  position: absolute;

  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_BackIcon = styled.img`
  width: 3.4rem;
`;

const S_Title = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const S_SearchButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  right: 2.1rem;

  width: 3.4rem;
  height: 3.4rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_SearchIcon = styled.img`
  width: 2.4rem;
  height: 2.4rem;
`;
