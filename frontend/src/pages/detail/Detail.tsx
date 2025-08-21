import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import { getMentoringDetail } from './apis/getMentoringDetail';
import ApplySection from './components/ApplySection/ApplySection';
import Certificates from './components/Certificates/Certificates';
import DetailHeader from './components/DetailHeader/DetailHeader';
import DetailReview from './components/DetailReview/DetailReview';
import Introduction from './components/Introduction/Introduction';
import MentorSummary from './components/MentorSummary/MentorSummary';
import Profile from './components/Profile/Profile';

import type { MentoringResponse } from './types/MentoringResponse';
import { captureSentryError } from '../../common/utils/captureSentryError';

type TapType = 'detail' | 'review';

function Detail() {
  const { mentoringId } = useParams();
  const [data, setData] = useState<MentoringResponse | null>(null);

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

  const [selected, setSelected] = useState<TapType>('detail');

  const handleClick = (selectedType: TapType) => {
    setSelected(selectedType);
  };

  if (!data) {
    return <div>로딩 중...</div>;
  }

  return (
    <>
      <DetailHeader />
      <StyledContainer>
        <StyledMentorInfoWrapper>
          <Profile
            profileImg={data.profileImageUrl}
            mentorName={data.mentorName}
            categories={data.categories}
            ratingAverage={data.ratingAverage}
            ratingCount={data.ratingCount}
          />
          <MentorSummary
            introduction={data.introduction}
            career={data.career}
            certificates={data.certificates}
          />
        </StyledMentorInfoWrapper>
        <StyledTapWrapper>
          <StyledTap
            onClick={() => handleClick('detail')}
            selected={selected === 'detail'}
          >
            상세보기
          </StyledTap>
          <StyledTap
            onClick={() => handleClick('review')}
            selected={selected === 'review'}
          >
            리뷰
          </StyledTap>
          <StyledTapIndicator selected={selected} />
        </StyledTapWrapper>
        <StyledContentWrapper>
          {selected === 'detail' ? (
            <StyledDetailWrapper>
              <Introduction content={data.content} />
              <StyledLine />
              <Certificates certificates={data.certificates} />
            </StyledDetailWrapper>
          ) : (
            <DetailReview
              mentoringId={data.id}
              ratingAverage={data.ratingAverage}
              ratingCount={data.ratingCount}
            />
          )}
        </StyledContentWrapper>
      </StyledContainer>
      <ApplySection price={data.price} mentoringId={mentoringId} />
    </>
  );
}

export default Detail;

const StyledContainer = styled.div`
  margin-bottom: 10rem;
  padding: 0 2rem;
`;

const StyledMentorInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
`;

const StyledTapWrapper = styled.div`
  display: flex;
  flex-direction: row;
  position: relative;

  width: 100%;
  padding: 1rem;
`;

const StyledTap = styled.p<{ selected: boolean }>`
  width: 50%;
  cursor: pointer;

  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;

const StyledTapIndicator = styled.div<{ selected: 'detail' | 'review' }>`
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

const StyledContentWrapper = styled.div`
  display: flex;

  width: 100%;
  padding-top: 2rem;
`;

const StyledDetailWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  width: 100%;
`;

const StyledLine = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
