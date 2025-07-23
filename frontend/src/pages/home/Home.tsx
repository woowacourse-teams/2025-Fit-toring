import styled from '@emotion/styled';

import Footer from '../../common/components/Footer/Footer';

import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorOverview from './components/MentorOverview/MentorOverview';
import Slogan from './components/Slogan/Slogan';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';

function Home() {
  return (
    <StyledContainer>
      <HomeHeader />
      <StyledContents>
        <Slogan />
        <MentorOverview mentorCount={6} />
        <SpecialtyFilterModalButton handleOpenModal={() => {}} />
      </StyledContents>
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

const StyledContents = styled.main`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
  padding: 0 1.4rem;
`;
