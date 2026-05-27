import styled from '@emotion/styled';
import { Link, useLocation } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

const PATH_TITLE: Record<string, string> = {
  [PAGE_URL.CREATED_MENTORING]: '운영하는 멘토링',
  [PAGE_URL.PARTICIPATED_MENTORING]: '수강하는 멘토링',
  [PAGE_URL.EDIT_PROFILE]: '프로필 수정',
};

function MyPageHeader() {
  const { pathname } = useLocation();
  const title = PATH_TITLE[pathname] ?? '마이페이지';

  return (
    <Header>
      <S_Wrapper>
        <S_LogoLink to={PAGE_URL.MY_PAGE}>
          <S_Img src={backIcon} alt="마이페이지 홈으로 돌아가기" />
        </S_LogoLink>
        <S_Title>{title}</S_Title>

        <MenuDropDown />
      </S_Wrapper>
    </Header>
  );
}

export default MyPageHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
  padding: 1.4rem 1.1rem;
`;

const S_LogoLink = styled(Link)`
  display: flex;

  width: 3.5rem;
  height: auto;
  aspect-ratio: 1 / 1;

  background: none;
  cursor: pointer;
`;

const S_Img = styled.img`
  width: 100%;
  height: 100%;
`;

const S_Title = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
