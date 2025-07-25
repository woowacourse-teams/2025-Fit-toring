import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';

function DetailHeader() {
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
        <StyledTitle>상세 정보</StyledTitle>
      </StyledHeaderWrapper>
    </Header>
  );
}

export default DetailHeader;

const StyledHeaderWrapper = styled.div`
  display: flex;
  height: 100%;
  align-items: center;
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
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}

  flex-grow: 1;

  text-align: center;
`;
