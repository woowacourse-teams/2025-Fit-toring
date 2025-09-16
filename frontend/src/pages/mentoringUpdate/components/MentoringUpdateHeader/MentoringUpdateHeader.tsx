import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';

function MentoringUpdateHeader() {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <Header>
      <S_HeaderWrapper>
        <S_BackButton onClick={handleBackButtonClick}>
          <S_Img src={backIcon} alt="뒤로가기 버튼" />
        </S_BackButton>
        <S_Title>멘토링 수정</S_Title>
      </S_HeaderWrapper>
    </Header>
  );
}

export default MentoringUpdateHeader;

const S_HeaderWrapper = styled.div`
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

const S_Img = styled.img`
  width: 3.4rem;
`;

const S_Title = styled.h3`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
