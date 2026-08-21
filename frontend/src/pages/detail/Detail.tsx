import { useRef } from 'react';

import styled from '@emotion/styled';
import { useLocation, useParams } from 'react-router-dom';

import SEO from '../../common/components/SEO/SEO';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { captureSentryError } from '../../common/utils/captureSentryError';

import ApplySection from './components/ApplySection/ApplySection';
import Certificates from './components/Certificates/Certificates';
import DetailHeader from './components/DetailHeader/DetailHeader';
import DetailReview from './components/DetailReview/DetailReview';
import DetailSkeleton from './components/DetailSkeleton/DetailSkeleton';
import Introduction from './components/Introduction/Introduction';
import ProfileSection from './components/ProfileSection/ProfileSection';
import useMentoringDetail from './hooks/useMentoringDetail';
import useTabs from './hooks/useTabs';
import { buildMentoringDetailSEO } from './utils/buildMentoringDetailSEO';

type TapType = 'detail' | 'review';

function Detail() {
  const location = useLocation();
  const state = location.state as { tab?: TapType };

  const { mentoringId } = useParams();

  const { data, isPending, isError, error } = useMentoringDetail(mentoringId!);

  const { selectedTab, selectTab } = useTabs<TapType>(state?.tab ?? 'detail');

  const contentWrapperRef = useRef<HTMLDivElement | null>(null);
  const certificateSectionRef = useRef<HTMLHeadingElement | null>(null);

  const handleTapClick = (tab: TapType) => {
    selectTab(tab);
    requestAnimationFrame(() => {
      contentWrapperRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      });
    });
  };

  const handleCertificateShowButton = () => {
    selectTab('detail');
    certificateSectionRef.current?.focus();
  };

  if (isError && error) {
    console.error('fetchData 실패', error);
    captureSentryError({
      error,
      level: 'warning',
      feature: 'detail',
      step: 'mentoring-detail-fetch',
    });

    return <div>데이터를 불러오는 중에 오류가 발생했습니다.</div>;
  }

  if (isPending || !data) {
    return (
      <>
        <DetailHeader />
        <DetailSkeleton />
      </>
    );
  }

  return (
    <>
      <SEO {...buildMentoringDetailSEO(data, mentoringId!)} />
      <S_SkipLink href="#apply-section">신청 버튼 바로가기</S_SkipLink>
      <DetailHeader />

      <S_Container>
        <S_MentorInfoWrapper>
          <ProfileSection
            profileImg={data.profileImageUrl}
            mentorName={data.mentorName}
            categories={data.categories}
            ratingAverage={data.ratingAverage}
            ratingCount={data.ratingCount}
            introduction={data.introduction}
            onCertificateShowButton={handleCertificateShowButton}
          />
        </S_MentorInfoWrapper>
        <S_TapWrapper selectedTab={selectedTab}>
          <S_Tap
            onClick={() => handleTapClick('detail')}
            selected={selectedTab === 'detail'}
          >
            상세보기
          </S_Tap>
          <S_Tap
            onClick={() => handleTapClick('review')}
            selected={selectedTab === 'review'}
          >
            리뷰
          </S_Tap>
        </S_TapWrapper>
        <S_ContentWrapper ref={contentWrapperRef}>
          {selectedTab === 'detail' ? (
            <S_DetailWrapper>
              <Introduction content={data.content} />
              <S_Line />
              <Certificates
                certificates={data.certificates}
                ref={certificateSectionRef}
              />
            </S_DetailWrapper>
          ) : (
            <DetailReview
              mentoringId={data.id}
              ratingAverage={data.ratingAverage}
              ratingCount={data.ratingCount}
              loadingComponent={
                <S_ReviewLoadingWrapper>
                  <LoadingSpinner />
                </S_ReviewLoadingWrapper>
              }
            />
          )}
        </S_ContentWrapper>
      </S_Container>
      <ApplySection
        id="apply-section"
        price={data.price}
        mentoringId={mentoringId}
      />
    </>
  );
}

export default Detail;

const S_Container = styled.div`
  margin-bottom: 12rem;
`;

const S_MentorInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
`;

const S_TapWrapper = styled.div<{ selectedTab: TapType }>`
  display: flex;
  position: relative;

  width: 100%;

  &::after {
    content: '';

    position: absolute;
    bottom: 0;
    left: 0;

    width: 50%;
    height: 2px;

    background-color: ${({ theme }) => theme.FONT.B01};

    transform: translateX(
      ${({ selectedTab }) => (selectedTab === 'detail' ? '0' : '100%')}
    );
    transition: transform 0.28s ease;
  }
`;

const S_Tap = styled.div<{ selected: boolean }>`
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;

  cursor: pointer;

  padding: 1.6rem 0;
  border-bottom: 2px solid ${({ theme }) => theme.SYSTEM.GRAY50};

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ selected, theme }) =>
    selected ? theme.FONT.B01 : theme.SYSTEM.GRAY500};

  transition: color 0.25s ease;

  ${({ selected, theme }) =>
    selected ? theme.TYPOGRAPHY.B2_B : theme.TYPOGRAPHY.B3_R};
`;

const S_ContentWrapper = styled.div`
  display: flex;

  width: 100%;
  padding: 3rem 2.7rem 0;
  cursor: pointer;
`;

const S_DetailWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  width: 100%;
`;

const S_Line = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_ReviewLoadingWrapper = styled.div`
  display: flex;
  flex-grow: 1;
  align-items: center;
  justify-content: center;

  width: 100%;
  min-height: 24rem;
`;

const S_SkipLink = styled.a`
  position: absolute;
  top: 0;
  left: 0;
  transform: translateY(-100%);

  z-index: 9999;

  padding: 1.2rem 2rem;
  border-radius: 0 0 8px 8px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY800};

  color: ${({ theme }) => theme.BG.WHITE};

  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  transition: transform 0.2s ease;

  &:focus {
    transform: translateY(0);
  }
`;
