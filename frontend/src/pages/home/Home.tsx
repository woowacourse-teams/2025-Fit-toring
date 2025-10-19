import { useCallback, useEffect, useRef, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import ReactGA from 'react-ga4';
import { useNavigate } from 'react-router-dom';

import { getMineMentoring } from '../../common/apis/getMineMentoring';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import Button from '../../common/components/Button/Button';
import { PAGE_URL } from '../../common/constants/url';
import { THEME } from '../../common/styles/theme';

import { getMentorListByPage } from './apis/getMentorListByPage';
import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorCardItem from './components/MentorCardItem/MentorCardItem';
import MentorCardList from './components/MentorCardList/MentorCardList';
import SortDropDown from './components/SortDropDown/SortDropDown';
import SpecialtyCheckbox from './components/SpecialtyCheckbox/SpecialtyCheckbox';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';
import useSort from './hooks/useSortKey';

import type { SortKey } from './hooks/useSortKey';
import type { MentorInformation } from './types/MentorInformation';
import type { Specialty } from '../../common/types/Specialty';

const convertSelectedSpecialtiesToParams = (
  selectedSpecialties: Specialty[],
): Record<string, string> => {
  const params: Record<string, string> = {};
  params['categoryIds'] = selectedSpecialties.map(({ id }) => id).join(',');

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

  const handleCloseModal = () => {
    setModalOpened(false);
  };

  const { sortKey, changeSortKey } = useSort();

  const getSortedMentors = async (sortKey: SortKey) => {
    const data = await getMentorListByPage({
      params: {
        ...convertSelectedSpecialtiesToParams(selectedSpecialties),
        sortKey,
      },
    });

    return data;
  };

  const fetchSortedMentors = async (sortKey: SortKey) => {
    const data = await getSortedMentors(sortKey);
    const {
      mentoringSummaryResponses,
      hasNext: hasNewNext,
      nextCursorCode,
    } = data;

    setMentorList(mentoringSummaryResponses);
    setHasNext(hasNewNext);
    setCursorCode(nextCursorCode);
  };

  const handleSortButtonClick = async (option: SortKey) => {
    changeSortKey(option);
    await fetchSortedMentors(option);
  };

  const [selectedSpecialties, setSelectedSpecialties] = useState<Specialty[]>(
    [],
  );

  const handleApply = async (specialties: Specialty[]) => {
    setSelectedSpecialties(specialties);
    handleCloseModal();
    await fetchFilteredMentors(specialties);
  };

  const handleSelectedSpecialtyChange = async (specialty: Specialty) => {
    setSelectedSpecialties((prev) => {
      const hasSpecialty = prev.find(
        (prevSpecialty) => prevSpecialty.id === specialty.id,
      );
      return hasSpecialty
        ? prev.filter((prevSpecialty) => prevSpecialty.id !== specialty.id)
        : [...prev, specialty];
    });

    await fetchFilteredMentors(
      selectedSpecialties.filter(
        (prevSpecialty) => prevSpecialty.id !== specialty.id,
      ),
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

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getMineMentoring();
        setMyMentoringId(response.id);
      } catch (error) {
        console.error(error);
        setMyMentoringId(null);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    if (!authenticated) {
      setMyMentoringId(null);
    }
  }, [authenticated]);

  const [mentorList, setMentorList] = useState<MentorInformation[]>([]);
  const [hasNext, setHasNext] = useState(true);
  const [cursorCode, setCursorCode] = useState<string | null>(null);
  const elementRef = useRef<HTMLLIElement>(null);

  const fetchMentorData = useCallback(async () => {
    const data = await getMentorListByPage({
      params: cursorCode
        ? {
            ...convertSelectedSpecialtiesToParams(selectedSpecialties),
            cursorCode,
            sortKey,
          }
        : {
            ...convertSelectedSpecialtiesToParams(selectedSpecialties),
            sortKey,
          },
    });

    return data;
  }, [cursorCode, selectedSpecialties, sortKey]);

  useEffect(() => {
    const callback = async (entries: IntersectionObserverEntry[]) => {
      if (entries[0].isIntersecting && hasNext) {
        const data = await fetchMentorData();
        const {
          mentoringSummaryResponses,
          hasNext: hasNewNext,
          nextCursorCode,
        } = data;

        setHasNext(hasNewNext);
        setMentorList((prev) => [...prev, ...mentoringSummaryResponses]);
        setCursorCode(nextCursorCode);
      }
    };

    const io = new IntersectionObserver(callback);
    if (elementRef.current) {
      io.observe(elementRef.current);
    }
    return () => io.disconnect();
  }, [fetchMentorData, hasNext]);

  const getFilteredMentors = async (selectedSpecialties: Specialty[]) => {
    const data = await getMentorListByPage({
      params: {
        ...convertSelectedSpecialtiesToParams(selectedSpecialties),
        sortKey,
      },
    });

    return data;
  };

  const fetchFilteredMentors = async (selectedSpecialties: Specialty[]) => {
    const data = await getFilteredMentors(selectedSpecialties);
    const {
      mentoringSummaryResponses,
      hasNext: hasNewNext,
      nextCursorCode,
    } = data;

    setMentorList(mentoringSummaryResponses);
    setHasNext(hasNewNext);
    setCursorCode(nextCursorCode);
  };

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
            handleSortButtonClick={handleSortButtonClick}
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
          {mentorList.map((mentor) => (
            <MentorCardItem key={mentor.id} mentor={mentor} />
          ))}
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
