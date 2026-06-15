import styled from '@emotion/styled';
import { useLocation, useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';
import MenuDropDown from '../MenuDropDown/MenuDropDown';

const PATH_TITLE: Record<string, string> = {
  [PAGE_URL.CREATED_MENTORING]: '운영하는 멘토링',
  [PAGE_URL.PARTICIPATED_MENTORING]: '수강하는 멘토링',
  [PAGE_URL.EDIT_PROFILE]: '프로필 수정',
  [PAGE_URL.SETTINGS]: '설정',
  [PAGE_URL.APP_INSTALL_GUIDE]: '앱 설치 안내',
};

function MyPageHeader() {
  const { pathname } = useLocation();
  const title = PATH_TITLE[pathname] ?? '마이페이지';

  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <Header>
      <S_Wrapper>
        <S_BackButton onClick={handleBackButtonClick}>
          <S_BackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </S_BackButton>
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
