import styled from '@emotion/styled';

import FitnessQuestionFlow from './components/FitnessQuestionFlow/FitnessQuestionFlow';
import Footer from './components/Footer/Footer';
import Introduce from './components/Introduce/Introduce';
import Slogan from './components/Slogan/Slogan';
import UserLevelGuide from './components/UserLevelGuide/UserLevelGuide';

function Landing() {
  return (
    <div>
      <Slogan />
      <FitnessQuestionFlow />
      <Introduce />
      <UserLevelGuide />
      <StyledButtonSection>
        <StyledButton>시작하기</StyledButton>
      </StyledButtonSection>
      <Footer />
    </div>
  );
}

export default Landing;

const StyledButtonSection = styled.div`
  padding-top: 5rem;
  padding-bottom: 10rem;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const StyledButton = styled.button`
  width: 13rem;
  border-radius: 7px;
  padding: 1.5rem 2rem;
  border: 2px solid #e3e3e3;
  background: ${({ theme }) => theme.SYSTEM.GRAY900};
  color: white;
  border: none;
  text-align: center;
  font-size: 1.7rem;
  font-weight: 700;
  cursor: pointer;
`;
