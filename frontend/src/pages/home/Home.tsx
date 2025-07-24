import { useState } from 'react';

import styled from '@emotion/styled';

import Footer from '../../common/components/Footer/Footer';

import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorOverview from './components/MentorOverview/MentorOverview';
import MentorCardItem from './components/MentorCardItem/MentorCardItem';
import MentorCardList from './components/MentorCardList/MentorCardList';
import Slogan from './components/Slogan/Slogan';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';

function Home() {
  const [modalOpened, setModalOpened] = useState(true);
  const handleOpenModal = () => {
    setModalOpened(true);
  };
  const handleCloseModal = () => {
    setModalOpened(false);
  };

  return (
    <StyledContainer>
      <HomeHeader />
      <StyledContents>
        <Slogan />
        <MentorOverview mentorCount={6} />
        <SpecialtyFilterModalButton handleOpenModal={handleOpenModal} />
        <SpecialtyFilterModal
          opened={modalOpened}
          selectedSpecialties={[]}
          handleCloseModal={handleCloseModal}
          handleReset={() => {}}
          handleApply={() => {}}
        />
        <MentorCardList>
          <MentorCardItem />
        </MentorCardList>
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
