import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';

function IdentityVerificationHeader() {
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
        <S_Title>본인 인증</S_Title>
      </S_Wrapper>
    </Header>
  );
}

export default IdentityVerificationHeader;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const S_BackButton = styled.button`
  position: absolute;

  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_Title = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const S_BackIcon = styled.img`
  width: 3.4rem;
`;
