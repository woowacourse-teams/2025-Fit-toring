import { useCallback } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import ReactGA from 'react-ga4';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import Button from '../../common/components/Button/Button';
import { PAGE_URL } from '../../common/constants/url';
import { THEME } from '../../common/styles/theme';

import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorCardList from './components/MentorCardList/MentorCardList';
import MentorCardListContent from './components/MentorCardListContent/MentorCardListContent';
import SortDropDown from './components/SortDropDown/SortDropDown';
import SpecialtyCheckbox from './components/SpecialtyCheckbox/SpecialtyCheckbox';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';
import useInfiniteScroll from './hooks/useInfiniteScroll';
import useMentorList from './hooks/useMentorList';
import useModal from './hooks/useModal';
import useMyMentoringId from './hooks/useMyMentoringId';
import useSort from './hooks/useSortKey';
import useSpecialtyFilter from './hooks/useSpecialtyFilter';

import type { SortKey } from './hooks/useSortKey';
import type { Specialty } from '../../common/types/Specialty';

function Home() {
  const { modalOpened, openModal, closeModal } = useModal();

  const { authenticated } = useAuth();

  const { myMentoringId } = useMyMentoringId(authenticated);

  const navigate = useNavigate();

  const handleOpenModal = () => {
    openModal();
    ReactGA.event({
      category: 'Specialty Filter',
      action: 'Open Specialty Filter Modal',
      label: '전문 분야 필터',
    });
  };

  const handleCloseModal = () => {
    closeModal();
  };

  const { sortKey, changeSortKey } = useSort();

  const {
    fetchInitialMentors,
    fetchMoreMentors,
    mentorList,
    hasNext,
    cursorCode,
    isLoading,
  } = useMentorList();

  const handleSortButtonClick = async (option: SortKey) => {
    changeSortKey(option);

    await fetchInitialMentors(selectedSpecialties, option);
  };

  const { selectedSpecialties, applySpecialties, toggleSpecialty } =
    useSpecialtyFilter();

  const handleApply = async (specialties: Specialty[]) => {
    applySpecialties(specialties);
    handleCloseModal();

    await fetchInitialMentors(specialties, sortKey);
  };

  const handleSelectedSpecialtyChange = async (specialty: Specialty) => {
    toggleSpecialty(specialty);

    await fetchInitialMentors(
      selectedSpecialties.filter(
        (prevSpecialty) => prevSpecialty.id !== specialty.id,
      ),
      sortKey,
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

  const fetchNextPage = useCallback(async () => {
    await fetchMoreMentors(selectedSpecialties, sortKey, cursorCode);
  }, [cursorCode, fetchMoreMentors, selectedSpecialties, sortKey]);

  const { elementRef } = useInfiniteScroll<HTMLLIElement>(
    fetchNextPage,
    hasNext,
  );

  return (
    <S_Container>
      <HomeHeader />
      <S_ActionWrapper>
        <S_FilterWrapper>
          <SpecialtyFilterModalButton handleOpenModal={handleOpenModal} />
          <SpecialtyFilterModal
            opened={modalOpened}
            handleCloseModal={handleCloseModal}
            selectedSpecialties={selectedSpecialties}
            handleApplyFinalSpecialties={handleApply}
          />
          <SortDropDown
            onSortButtonClick={handleSortButtonClick}
            currentSortKey={sortKey}
          />
        </S_FilterWrapper>
        <Button onClick={handleMentoringCreation} customStyle={customStyle}>
          {myMentoringId === null ? '멘토링 개설하기' : '멘토링 관리하기'}
        </Button>
      </S_ActionWrapper>
      <S_Contents>
        <S_CheckboxWrapper>
          {selectedSpecialties.map((specialty) => (
            <SpecialtyCheckbox
              key={specialty.id}
              specialty={specialty.title}
              checked={selectedSpecialties.includes(specialty)}
              disabled={false}
              onChange={() => handleSelectedSpecialtyChange(specialty)}
            />
          ))}
        </S_CheckboxWrapper>
        <MentorCardList>
          <MentorCardListContent
            isLoading={isLoading}
            mentorList={mentorList}
            hasFilter={selectedSpecialties.length > 0}
          />
          <S_Trigger ref={elementRef} />
        </MentorCardList>
      </S_Contents>
      {/* <Footer>
        <Feedback />
      </Footer> */}
    </S_Container>
  );
}

export default Home;

const customStyle = css`
  width: 12.9rem;
  height: 3.4rem;
  border: 1px solid ${THEME.SYSTEM.GRAY300};
  border-radius: 5px;

  background-color: ${THEME.BG.WHITE};

  color: ${THEME.SYSTEM.MAIN600};
  ${THEME.TYPOGRAPHY.B4_B};
`;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;

  min-height: 100%;
`;

const S_ActionWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 1.4rem;
`;

const S_FilterWrapper = styled.div`
  display: flex;
  gap: 0.7rem;
`;

const S_Trigger = styled.li`
  visibility: hidden;
  position: absolute;
  bottom: 1rem;

  width: 100%;
  height: 21.5rem;
`;

const S_Contents = styled.main`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  align-items: center;
`;

const S_CheckboxWrapper = styled.div`
  display: flex;
  gap: 0.5rem;

  width: 100%;
  padding: 0 2rem;
`;
