import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import ReactGA from 'react-ga4';

import Footer from '../../common/components/Footer/Footer';

import { getMentorList } from './apis/getMentorList';
import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorCardItem from './components/MentorCardItem/MentorCardItem';
import MentorCardList from './components/MentorCardList/MentorCardList';
import MentorOverview from './components/MentorOverview/MentorOverview';
import Slogan from './components/Slogan/Slogan';
import SpecialtyCheckbox from './components/SpecialtyCheckbox/SpecialtyCheckbox';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';

import type { MentorInformation } from './types/MentorInformation';

const convertSelectedSpecialtiesToParams = (
  selectedSpecialties: string[],
): Record<string, string> => {
  const params: Record<string, string> = {};
  selectedSpecialties.forEach((specialty, index) => {
    params[`categoryTitle${index + 1}`] = specialty;
  });

  return params;
};

function Home() {
  const [modalOpened, setModalOpened] = useState(false);
  const handleOpenModal = () => {
    setModalOpened(true);
    ReactGA.event({
      category: 'Specialty Filter',
      action: 'Open Specialty Filter Modal',
      label: '전문 분야 필터',
    });
  };
  const handleCloseModal = () => {
    setModalOpened(false);
  };

  const [selectedSpecialties, setSelectedSpecialties] = useState<string[]>([]);

  const handleApply = (specialties: string[]) => {
    setSelectedSpecialties(specialties);
    handleCloseModal();
  };

  const handleSelectedSpecialtyChange = (specialty: string) => {
    setSelectedSpecialties((prev) =>
      prev.includes(specialty)
        ? prev.filter((prevSpecialty) => prevSpecialty !== specialty)
        : [...prev, specialty],
    );
  };

  const [mentorList, setMentorList] = useState<MentorInformation[]>([]);

  useEffect(() => {
    const fetchMentorData = async () => {
      try {
        const data = await getMentorList({
          params: convertSelectedSpecialtiesToParams(selectedSpecialties),
        });

        setMentorList(data);
      } catch (error) {
        console.error('멘토 데이터 가져오기 실패:', error);
      }
    };

    fetchMentorData();
  }, [selectedSpecialties]);

  return (
    <StyledContainer>
      <HomeHeader />
      <StyledContents>
        <Slogan />
        <MentorOverview mentorCount={mentorList.length} />
        <SpecialtyFilterModalButton handleOpenModal={handleOpenModal} />
        <SpecialtyFilterModal
          opened={modalOpened}
          handleCloseModal={handleCloseModal}
          selectedSpecialties={selectedSpecialties}
          handleApplyFinalSpecialties={handleApply}
        />
        <StyledCheckboxWrapper>
          {selectedSpecialties.map((specialty) => (
            <SpecialtyCheckbox
              key={specialty}
              specialty={specialty}
              checked={selectedSpecialties.includes(specialty)}
              disabled={false}
              onChange={() => handleSelectedSpecialtyChange(specialty)}
            />
          ))}
        </StyledCheckboxWrapper>
        <MentorCardList>
          {mentorList.map((mentor) => (
            <MentorCardItem key={mentor.id} mentor={mentor} />
          ))}
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
`;

const StyledCheckboxWrapper = styled.div`
  display: flex;
  gap: 0.5rem;

  width: 100%;
  padding: 0 2rem;
`;
