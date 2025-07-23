import styled from '@emotion/styled';

import Footer from '../../common/components/Footer/Footer';

import HomeHeader from './components/HomeHeader/HomeHeader';
import Slogan from './components/Slogan/Slogan';

function Home() {
  return (
    <StyledContainer>
      <HomeHeader />
      <Slogan />
      <StyledSection>section</StyledSection>
      <Footer>문의: fitoring7@gmail.com</Footer>
    </StyledContainer>
  );
}

export default Home;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;

  height: 100%;
`;

const StyledSection = styled.div`
  flex-grow: 1;
`;
