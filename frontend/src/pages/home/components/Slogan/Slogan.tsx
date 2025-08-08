import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';

function Slogan() {
  const { authenticated } = useAuth();
  const navigate = useNavigate();

  const handleMentoringCreation = () => {
    if (authenticated) {
      navigate(PAGE_URL.MENTORING_CREATE);
    } else {
      navigate(PAGE_URL.LOGIN);
    }
  };

  return (
    <StyledContainer>
      <StyledTitle>
        내가 알고 싶은 운동 & 식단, <StyledStrong>온라인</StyledStrong>으로
        편하게 물어봐요!
      </StyledTitle>

      <Button onClick={handleMentoringCreation}>멘토링 개설하기</Button>
    </StyledContainer>
  );
}

export default Slogan;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.5rem;

  width: 100%;
  height: auto;
  padding: 3rem 1.4rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background: linear-gradient(
    180deg,
    ${({ theme }) => theme.SYSTEM.MAIN50} 0%,
    ${({ theme }) => theme.BG.WHITE} 100%
  );
`;

const StyledTitle = styled.h1`
  width: 27.2rem;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H1_R}
`;

const StyledStrong = styled.strong`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.H1_B}
`;
