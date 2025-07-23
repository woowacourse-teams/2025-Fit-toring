import styled from '@emotion/styled';

import Footer from '../../common/components/Footer/Footer';

import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorOverview from './components/MentorOverview/MentorOverview';
import Slogan from './components/Slogan/Slogan';
import SpecialtiesModalButton from './components/SpecialtiesModalButton/SpecialtiesModalButton';

function Home() {
  return (
    <StyledContainer>
      <HomeHeader />
      <StyledContents>
        <Slogan />
        <MentorOverview mentorCount={6} />
        <SpecialtiesModalButton handleOpenModal={() => {}} />
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
  gap: 2rem;
  padding: 0 1.4rem;
`;
