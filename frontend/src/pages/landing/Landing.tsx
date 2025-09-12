import styled from '@emotion/styled';

import landing1 from '../../common/assets/images/landing1.png';
import landing2 from '../../common/assets/images/landing2.png';
import landing3 from '../../common/assets/images/landing3.png';

import Slogan from './components/Slogan/Slogan';

function Landing() {
  return (
    <div>
      <Slogan />
      <StyledLandingBgWithColor>
        <StyledImg src={landing1} alt="랜딩1" />
      </StyledLandingBgWithColor>
      <StyledLandingBg>
        <StyledImg src={landing2} alt="랜딩2" />
      </StyledLandingBg>
      <StyledLandingBg>
        <StyledImg src={landing3} alt="랜딩3" />
      </StyledLandingBg>
      <StyledButtonSection>
        <StyledButton>시작하기</StyledButton>
      </StyledButtonSection>
    </div>
  );
}

export default Landing;

const StyledLandingBg = styled.div`
  display: flex;
  justify-content: center;
  margin: 0 auto;
  overflow: hidden;
`;

const StyledLandingBgWithColor = styled.div`
  display: flex;
  justify-content: center;
  margin: 0 auto;
  background: ${({ theme }) => `
  linear-gradient(
    180deg,
    ${theme.SYSTEM.GRAY50} 0%,
    #fff 100%
  )
`};
  overflow: hidden;
`;

const StyledImg = styled.img`
  width: 130%;
`;

const StyledButtonSection = styled.div`
  height: 15rem;
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
