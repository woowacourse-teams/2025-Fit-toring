import styled from '@emotion/styled';

import { apiClient } from '../../common/apis/apiClient';
import Footer from '../../common/components/Footer/Footer';

import HomeHeader from './components/HomeHeader/HomeHeader';
import Slogan from './components/Slogan/Slogan';

function Home() {
  const data = apiClient.get({ endpoint: '' });
  return (
    <StyledContainer>
      <HomeHeader />
      <Slogan />
      {if (data){
        
      }}
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
