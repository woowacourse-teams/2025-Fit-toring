import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
} from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import ReactGA from 'react-ga4';
import { useNavigate } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import Button from '../../common/components/Button/Button';
import InstallPromptModal from '../../common/components/InstallPromptModal/InstallPromptModal';
import IOSInstallGuideModal from '../../common/components/IOSInstallGuideModal/IOSInstallGuideModal';
import NotificationPermissionModal from '../../common/components/NotificationPermissionModal/NotificationPermissionModal';
import PullToRefresh from '../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../common/components/PullToRefresh/utils';
import { PAGE_URL } from '../../common/constants/url';
import useAuthCheck from '../../common/hooks/useAuthCheck';
import useInfiniteScroll from '../../common/hooks/useInfiniteScroll';
import useNotification from '../../common/hooks/useNotification';
import usePWAInstall from '../../common/hooks/usePWAInstall';
import { THEME } from '../../common/styles/theme';
import {
  isIOS,
  isMobileViewport,
  isPWAStandalone,
} from '../../common/utils/deviceDetection';
import {
  markInstallPromptShown,
  shouldAutoShowInstallPromptOnHome,
} from '../../common/utils/installExposurePolicy';

import HomeHeader from './components/HomeHeader/HomeHeader';
import MentorCardList from './components/MentorCardList/MentorCardList';
import MentorCardListContent from './components/MentorCardListContent/MentorCardListContent';
import SortDropDown from './components/SortDropDown/SortDropDown';
import SpecialtyCheckbox from './components/SpecialtyCheckbox/SpecialtyCheckbox';
import SpecialtyFilterModal from './components/SpecialtyFilterModal/SpecialtyFilterModal';
import SpecialtyFilterModalButton from './components/SpecialtyFilterModalButton/SpecialtyFilterModalButton';
import useInfiniteMentorList from './hooks/useInfiniteMentorList';
import useModal from './hooks/useModal';
import useMyMentoringId from './hooks/useMyMentoringId';
import useSort from './hooks/useSortKey';
import useSpecialtyFilter from './hooks/useSpecialtyFilter';
import {
  clearHomeScrollY,
  getHomeScrollY,
  getMaxHomeScrollY,
  restoreHomeScrollY,
} from './utils/homeScrollStorage';

import type { SortKey } from './hooks/useSortKey';
import type { Specialty } from '../../common/types/Specialty';

type InstallModalType = 'ios' | 'android' | null;

function Home() {
  const contentsRef = useRef<HTMLElement | null>(null);
  const { modalOpened, openModal, closeModal } = useModal();
  const [installModalType, setInstallModalType] =
    useState<InstallModalType>(null);

  const { authenticated } = useAuth();

  const { myMentoringId } = useMyMentoringId(authenticated);

  const navigate = useNavigate();

  const {
    requestNotificationPermission,
    showModal: showNotificationModal,
    closeModal: closeNotificationModal,
  } = useNotification(authenticated);
  const { canInstall, hasInstalledBefore, promptInstall } = usePWAInstall();

  const handleAllowNotification = async () => {
    await requestNotificationPermission();
  };

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

  const { selectedSpecialties, applySpecialties, toggleSpecialty } =
    useSpecialtyFilter();

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    refetch,
  } = useInfiniteMentorList({
    selectedSpecialties,
    sortKey,
  });

  const mentorList =
    data?.pages.flatMap((page) => page.mentoringSummaryResponses) ?? [];

  const handleSortButtonClick = (option: SortKey) => {
    changeSortKey(option);
  };

  const handleApply = (specialties: Specialty[]) => {
    applySpecialties(specialties);
    handleCloseModal();
  };

  const handleSelectedSpecialtyChange = (specialty: Specialty) => {
    toggleSpecialty(specialty);
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

  const handleIntersect = useCallback(async () => {
    await fetchNextPage();
  }, [fetchNextPage]);

  const handleRefresh = useCallback(async () => {
    await refetch();
  }, [refetch]);

  const { targetRef } = useInfiniteScroll<HTMLLIElement>({
    isReady: !!hasNextPage && !isFetchingNextPage,
    onIntersect: handleIntersect,
  });

  useLayoutEffect(() => {
    if (isPending) {
      return;
    }

    const savedScrollY = getHomeScrollY();

    if (savedScrollY === null) {
      return;
    }

    const frameId = window.requestAnimationFrame(() => {
      const maxScrollY = getMaxHomeScrollY(contentsRef.current);

      if (savedScrollY > maxScrollY && hasNextPage) {
        if (isFetchingNextPage) {
          return;
        }

        fetchNextPage();
        return;
      }

      restoreHomeScrollY(
        Math.min(savedScrollY, maxScrollY),
        contentsRef.current,
      );
      clearHomeScrollY();
    });

    return () => window.cancelAnimationFrame(frameId);
  }, [
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending,
    mentorList.length,
  ]);

  useAuthCheck();

  const handleCloseAndroidInstallPrompt = useCallback(() => {
    markInstallPromptShown('android');
    setInstallModalType(null);
  }, []);

  const handleAndroidInstallClick = useCallback(async () => {
    if (hasInstalledBefore) {
      window.alert('이미 설치되었습니다.');
      return;
    }

    if (!canInstall) {
      window.alert('현재 브라우저에서는 앱 설치를 바로 실행할 수 없습니다.');
      return;
    }

    await promptInstall();
    handleCloseAndroidInstallPrompt();
  }, [
    canInstall,
    handleCloseAndroidInstallPrompt,
    hasInstalledBefore,
    promptInstall,
  ]);

  const handleCloseIOSInstallGuide = useCallback(() => {
    markInstallPromptShown('ios');
    setInstallModalType(null);
  }, []);

  useEffect(() => {
    if (!isMobileViewport()) {
      return;
    }

    const platform = isIOS() ? 'ios' : 'android';

    if (
      !shouldAutoShowInstallPromptOnHome({
        isStandalone: isPWAStandalone(),
        platform,
      })
    ) {
      return;
    }

    if (platform === 'ios') {
      setInstallModalType('ios');
      return;
    }

    if (!canInstall) {
      return;
    }

    setInstallModalType('android');
  }, [canInstall]);

  return (
    <S_Container>
      <InstallPromptModal
        opened={installModalType === 'android'}
        onCloseClick={handleCloseAndroidInstallPrompt}
        onLaterClick={handleCloseAndroidInstallPrompt}
        onInstallClick={handleAndroidInstallClick}
      />

      <IOSInstallGuideModal
        opened={installModalType === 'ios'}
        onCloseClick={handleCloseIOSInstallGuide}
        onLaterClick={handleCloseIOSInstallGuide}
      />

      <NotificationPermissionModal
        isOpen={showNotificationModal}
        onAllow={handleAllowNotification}
        onClose={closeNotificationModal}
      />

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
      <PullToRefresh
        enabled={isPullToRefreshEnabled()}
        onRefresh={handleRefresh}
      >
        <S_Contents ref={contentsRef}>
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
              isLoading={isPending}
              mentorList={mentorList}
              hasFilter={selectedSpecialties.length > 0}
            />
            <S_Trigger ref={targetRef} />
          </MentorCardList>
        </S_Contents>
      </PullToRefresh>
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

  color: ${THEME.SYSTEM.MAIN500};
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
