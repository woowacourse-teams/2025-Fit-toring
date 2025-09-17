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

  const handleClick = (selectedType: TapType) => {
    setSelected(selectedType);
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
          />
          {/* <MentorSummary
            introduction={data.introduction}
            career={data.career}
            certificates={data.certificates}
          /> */}
        </S_MentorInfoWrapper>
        <S_TapWrapper>
          <S_Tap
            onClick={() => handleClick('detail')}
            selected={selected === 'detail'}
          >
            상세보기
          </S_Tap>
          <S_Tap
            onClick={() => handleClick('review')}
            selected={selected === 'review'}
          >
            리뷰
          </S_Tap>
          <S_TapIndicator selected={selected} />
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
  /* margin-bottom: 10rem; */
  /* padding: 0 2rem; */
`;

const S_MentorInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
`;

const S_TapWrapper = styled.div`
  display: flex;
  flex-direction: row;
  position: relative;

  width: 100%;
  padding: 1rem;
`;

const S_Tap = styled.p<{ selected: boolean }>`
  width: 50%;
  cursor: pointer;

  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;

const S_TapIndicator = styled.div<{ selected: 'detail' | 'review' }>`
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 0;

  width: 50%;
  height: 1px;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  transition: transform 0.2s ease-in-out;

  transform: ${({ selected }) =>
    selected === 'detail' ? 'translateX(0%)' : 'translateX(100%)'};
`;

const S_ContentWrapper = styled.div`
  display: flex;

  width: 100%;
  padding-top: 2rem;
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
