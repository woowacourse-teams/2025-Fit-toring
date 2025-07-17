import styled from '@emotion/styled';

import Button from '../../../common/components/Button/Button';
import Footer from '../../../common/components/Footer/Footer';

function Home() {
  return (
    <StyledContainer>
      <Button></Button>
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
