import { useCallback, useEffect, useState } from 'react';

import styled from '@emotion/styled';
import ReactGA from 'react-ga4';

import Footer from '../../common/components/Footer/Footer';

import { getMentorList } from './apis/getMentorList';
import Feedback from './components/Feedback/Feedback';
import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorCardItem from './components/MentorCardItem/MentorCardItem';
import MentorCardList from './components/MentorCardList/MentorCardList';
import SpecialtyCheckbox from './components/SpecialtyCheckbox/SpecialtyCheckbox';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';

import type { MentorInformation } from './types/MentorInformation';
import { captureSentryError } from '../../common/utils/captureSentryError';
import SortButton from './components/SortButton/SortButton';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import { useNavigate } from 'react-router-dom';
import { PAGE_URL } from '../../common/constants/url';
import Button from '../../common/components/Button/Button';
import { css } from '@emotion/react';
import { THEME } from '../../common/styles/theme';

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
  const [myMentoringId, setMyMentoringId] = useState<null | number>(null);

  const { authenticated } = useAuth();
  const navigate = useNavigate();

  const handleOpenModal = () => {
    setModalOpened(true);
    ReactGA.event({
      category: 'Specialty Filter',
      action: 'Open Specialty Filter Modal',
      label: '전문 분야 필터',
    });
  };

  const handleSortButtonClick = () => {
    alert('기능 추가 예정입니다.');
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

  const handleMentoringCreation = () => {
    if (!authenticated) {
      navigate(PAGE_URL.LOGIN);
      return;
    }
    if (myMentoringId !== null) {
      navigate(PAGE_URL.CREATED_MENTORING);
      return;
    }

    navigate(PAGE_URL.MENTORING_CREATE);
  };

  const [mentorList, setMentorList] = useState<MentorInformation[]>([]);

  const fetchMentorData = useCallback(async () => {
    try {
      const data = await getMentorList({
        params: convertSelectedSpecialtiesToParams(selectedSpecialties),
      });
      setMentorList(data);
    } catch (error) {
      console.error('멘토 데이터 가져오기 실패:', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'home',
        step: 'mentor-data-fetch',
      });
    }
  }, [selectedSpecialties]);

  useEffect(() => {
    fetchMentorData();
  }, [fetchMentorData]);

  return (
    <StyledContainer>
      <HomeHeader />
      <StyledActionWrapper>
        <StyledFilterWrapper>
          <SpecialtyFilterModalButton handleOpenModal={handleOpenModal} />
          <SpecialtyFilterModal
            opened={modalOpened}
            handleCloseModal={handleCloseModal}
            selectedSpecialties={selectedSpecialties}
            handleApplyFinalSpecialties={handleApply}
          />
          <SortButton handleSortButtonClick={handleSortButtonClick} />
        </StyledFilterWrapper>
        <Button onClick={handleMentoringCreation} customStyle={customSytle}>
          {myMentoringId === null ? '멘토링 개설하기' : '멘토링 관리하기'}
        </Button>{' '}
      </StyledActionWrapper>

      <StyledContents>
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
      <Footer>
        <Feedback />
      </Footer>
    </StyledContainer>
  );
}

export default Home;

const customSytle = css`
  width: 12.9rem;
  height: 3.4rem;
  border-radius: 5px;
  border: 1px solid ${THEME.SYSTEM.GRAY300};
  background-color: ${THEME.BG.WHITE};
  color: ${THEME.SYSTEM.MAIN600};
  ${THEME.TYPOGRAPHY.B4_B};
`;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;

  min-height: 100%;
`;

const StyledActionWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.4rem;
`;

const StyledFilterWrapper = styled.div`
  display: flex;
  gap: 7px;
`;

const StyledContents = styled.main`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  align-items: center;
`;

const StyledCheckboxWrapper = styled.div`
  display: flex;
  gap: 0.5rem;

  width: 100%;
  padding: 0 2rem;
`;
