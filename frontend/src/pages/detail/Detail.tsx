import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useLocation, useParams } from 'react-router-dom';

import { getMentoringDetail } from '../../common/apis/getMentoringDetail';
import { captureSentryError } from '../../common/utils/captureSentryError';

import ApplySection from './components/ApplySection/ApplySection';
import Certificates from './components/Certificates/Certificates';
import DetailHeader from './components/DetailHeader/DetailHeader';
import DetailReview from './components/DetailReview/DetailReview';
import Introduction from './components/Introduction/Introduction';
import ProfileSection from './components/ProfileSection/ProfileSection';

import type { MentoringDetail } from '../../common/types/MentoringDetail';

type TapType = 'detail' | 'review';

function Detail() {
  const location = useLocation();
  const state = location.state as { tab?: TapType };

  const { mentoringId } = useParams();
  const [data, setData] = useState<MentoringDetail | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getMentoringDetail(mentoringId!);

        setData(response);
      } catch (error) {
        console.error('fetchData 실패', error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'detail',
          step: 'mentoring-detail-fetch',
        });
      }
    };
    fetchData();
  }, [mentoringId]);

  const [selected, setSelected] = useState<TapType>(state?.tab ?? 'detail');

  const handleTapClick = (selectedType: TapType) => {
    setSelected(selectedType);
  };

  const handleCertificateShowButton = () => {
    setSelected('detail');
  };

  if (!data) {
    return <div>로딩 중...</div>;
  }

  return (
    <>
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
            />
          )}
        </S_ContentWrapper>
      </S_Container>
      <ApplySection price={data.price} mentoringId={mentoringId} />
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
  padding: 4rem 2.7rem 0;
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
