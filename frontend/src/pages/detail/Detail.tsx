import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';
import { useLocation, useParams } from 'react-router-dom';

import { getMentoringDetail } from '../../common/apis/getMentoringDetail';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { captureSentryError } from '../../common/utils/captureSentryError';

import ApplySection from './components/ApplySection/ApplySection';
import Certificates from './components/Certificates/Certificates';
import DetailHeader from './components/DetailHeader/DetailHeader';
import DetailReview from './components/DetailReview/DetailReview';
import Introduction from './components/Introduction/Introduction';
import ProfileSection from './components/ProfileSection/ProfileSection';

type TapType = 'detail' | 'review';

function Detail() {
  const location = useLocation();
  const state = location.state as { tab?: TapType };

  const { mentoringId } = useParams();

  const { data, isError, error } = useQuery({
    queryKey: ['mentoringDetail', mentoringId],
    queryFn: () => getMentoringDetail(mentoringId!),
  });

  useEffect(() => {
    if (isError && error) {
      console.error('fetchData 실패', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'detail',
        step: 'mentoring-detail-fetch',
      });
    }
  }, [isError, error]);

  const [selected, setSelected] = useState<TapType>(state?.tab ?? 'detail');
  const [scrollY, setScrollY] = useState(0);

  const handleTapClick = (selectedType: TapType) => {
    setSelected(selectedType);
    setScrollY(window.scrollY);
  };

  const handleCertificateShowButton = () => {
    setSelected('detail');
    document.getElementById('certificate-section')?.focus();
  };

  if (!data) {
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
            selected={selected === 'detail'}
          >
            상세보기
          </S_Tap>
          <S_Tap
            onClick={() => handleTapClick('review')}
            selected={selected === 'review'}
          >
            리뷰
          </S_Tap>
        </S_TapWrapper>
        <S_ContentWrapper>
          {selected === 'detail' ? (
            <S_DetailWrapper>
              <Introduction content={data.content} />
              <S_Line />
              <Certificates certificates={data.certificates} />
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
  border-radius: 0 0 0.8rem 0.8rem;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY800};

  color: ${({ theme }) => theme.BG.WHITE};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};

  transition: transform 0.2s ease;

  &:focus {
    transform: translateY(0);
  }
`;
