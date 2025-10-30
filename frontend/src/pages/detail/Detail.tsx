import { useRef } from 'react';

import styled from '@emotion/styled';
import { useLocation, useParams } from 'react-router-dom';

import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { captureSentryError } from '../../common/utils/captureSentryError';

import ApplySection from './components/ApplySection/ApplySection';
import Certificates from './components/Certificates/Certificates';
import DetailHeader from './components/DetailHeader/DetailHeader';
import DetailReview from './components/DetailReview/DetailReview';
import Introduction from './components/Introduction/Introduction';
import ProfileSection from './components/ProfileSection/ProfileSection';
import useMentoringDetail from './hooks/useMentoringDetail';
import useScrollY from './hooks/useScrollY';
import useTabs from './hooks/useTabs';

type TapType = 'detail' | 'review';

function Detail() {
  const location = useLocation();
  const state = location.state as { tab?: TapType };

  const { mentoringId } = useParams();

  const { data, isPending, isError, error } = useMentoringDetail(mentoringId!);

  const { selectedTab, selectTab } = useTabs<TapType>(state?.tab ?? 'detail');

  const { scrollY, changeScrollY } = useScrollY();

  const certificateSectionRef = useRef<HTMLHeadingElement | null>(null);

  const handleTapClick = (tab: TapType) => {
    selectTab(tab);
    changeScrollY(window.scrollY);
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
    return <div>로딩 중...</div>;
  }

  return (
    <>
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
        <S_TapWrapper>
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
        <S_ContentWrapper>
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
                <S_SpinnerWrapper height={scrollY}>
                  <LoadingSpinner />
                </S_SpinnerWrapper>
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

const S_TapWrapper = styled.div`
  display: flex;

  width: 100%;
`;

const S_Tap = styled.div<{ selected: boolean }>`
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;

  cursor: pointer;

  padding: 1.6rem 0;
  border-top: 1px solid
    ${({ selected, theme }) => (selected ? 'none' : theme.SYSTEM.GRAY50)};
  border-bottom: 1px solid
    ${({ selected, theme }) => (selected ? 'none' : theme.SYSTEM.GRAY50)};

  background-color: ${({ selected, theme }) =>
    selected ? theme.SYSTEM.GRAY800 : theme.BG.WHITE};

  color: ${({ selected, theme }) =>
    selected ? theme.BG.WHITE : theme.FONT.B01};

  transition:
    background-color 0.25s ease,
    color 0.25s ease;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
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

const S_SpinnerWrapper = styled.div<{ height: number }>`
  display: flex;
  flex-grow: 1;
  align-items: center;
  justify-content: center;

  height: ${({ height }) => `${height}px`};
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

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};

  transition: transform 0.2s ease;

  &:focus {
    transform: translateY(0);
  }
`;
