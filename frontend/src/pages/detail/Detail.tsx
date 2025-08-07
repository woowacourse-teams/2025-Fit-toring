import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import { getMentoringDetail } from './apis/getMentoringDetail';
import ApplySection from './components/ApplySection/ApplySection';
import DetailHeader from './components/DetailHeader/DetailHeader';
import Introduction from './components/Introduction/Introduction';
import MentorSummary from './components/MentorSummary/MentorSummary';
import Profile from './components/Profile/Profile';

import type { MentoringResponse } from './types/MentoringResponse';

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
            profileImg={data.imageUrl}
            mentorName={data.mentorName}
            categories={data.categories}
          />
          <MentorSummary
            introduction={data.introduction}
            career={data.career}
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
              <ApplySection price={data.price} mentoringId={mentoringId} />
            </StyledDetailWrapper>
          ) : (
            <div>
              <p>리뷰 영역</p>
            </div>
          )}
        </StyledContentWrapper>
      </StyledContainer>
    </>
  );
}

export default Detail;

const StyledContainer = styled.div`
  padding: 0 2rem;
`;

const StyledMentorInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
`;

const StyledTapWrapper = styled.div`
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: row;
  padding: 1rem;
`;

const StyledTap = styled.p<{ selected?: boolean }>`
  width: 50%;
  cursor: pointer;
  text-align: center;

  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;

const StyledTapIndicator = styled.div<{ selected?: 'detail' | 'review' }>`
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50%;
  height: 1px;
  background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  transition: transform 0.2s ease-in-out;
  z-index: 0;

  transform: ${({ selected }) =>
    selected === 'detail' ? 'translateX(0%)' : 'translateX(100%)'};
`;

const StyledContentWrapper = styled.div`
  display: flex;
  width: 100%;
  transition: transform 0.3s ease-in-out;
  padding-top: 2rem;
`;

const StyledDetailWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
`;
