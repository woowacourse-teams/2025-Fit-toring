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

  if (!data) {
    return <div>로딩 중...</div>;
  }

  return (
    <StyledContainer>
      <DetailHeader />
      <Profile
        profileImg={data.imageUrl}
        mentorName={data.mentorName}
        categories={data.categories}
      />
      <MentorSummary introduction={data.introduction} career={data.career} />
      <Introduction />
      <ApplySection price={data.price} />
    </StyledContainer>
  );
}

export default Detail;

const StyledContainer = styled.div`
  display: flex;
  padding: 0 4.1rem 2rem;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
`;
