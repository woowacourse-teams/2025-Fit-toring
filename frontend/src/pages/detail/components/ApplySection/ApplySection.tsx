import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import Button from '../../../../common/components/Button/Button';

function ApplySection() {
  const navigate = useNavigate();
  const moveToBookingPage = () => {
    navigate('/booking');
  };
  return (
    <StyledContainer>
      <StyledWrapper>
        <p>15분 상담료</p>
        <strong>5,000원</strong>
      </StyledWrapper>
      <Button
        size="full"
        customStyle={css`
          font-size: 1.2rem;
        `}
        onClick={moveToBookingPage}
      >
        신청하기
      </Button>
    </StyledContainer>
  );
}

export default ApplySection;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 9.4rem;
  padding: 2rem 2.1rem 0.8rem;
  gap: 1rem;

  background-color: ${({ theme }) => theme.BG.GREEN};
`;

const StyledWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B02};
  }

  & strong {
    ${({ theme }) => theme.TYPOGRAPHY.H4_B};
    color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }
`;
