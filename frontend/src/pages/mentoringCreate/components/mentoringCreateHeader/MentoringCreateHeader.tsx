import Header from '../../../../common/components/Header/Header';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';

function MentoringCreateHeader() {
  const navigate = useNavigate();

  const handleMoveBack = () => {
    navigate(-1);
  };

  return (
    <Header>
      <StyledHeaderWrapper>
        <StyledBackButton onClick={handleMoveBack}>
          <StyledImg src={backIcon} alt="뒤로가기 버튼" />
        </StyledBackButton>
        <StyledTitle>멘토링 개설</StyledTitle>
      </StyledHeaderWrapper>
    </Header>
  );
}

export default MentoringCreateHeader;

const StyledHeaderWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const StyledBackButton = styled.button`
  position: absolute;

  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const StyledImg = styled.img`
  width: 3.4rem;
`;

const StyledTitle = styled.h3`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
