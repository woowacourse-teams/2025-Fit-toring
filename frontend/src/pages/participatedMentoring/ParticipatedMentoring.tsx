import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getParticipatedMentoringList } from './apis/getParticipatedMentoring';
import MentoringItem from './MentoringItem/MentoringItem';
import MentoringList from './MentoringList/MentoringList';

import type { ParticipatedMentoringType } from './types/participatedMentoring';

function ParticipatedMentoring() {
  const [participatedMentoringList, setParticipatedMentoringList] = useState<
    ParticipatedMentoringType[]
  >([]);

  useEffect(() => {
    const fetchParticipatedMentoringList = async () => {
      const data = await getParticipatedMentoringList();
      setParticipatedMentoringList(data);
    };

    fetchParticipatedMentoringList();
  }, []);

  return (
    <StyledContainer>
      <StyledTitle>참여한 멘토링</StyledTitle>
      <StyledWrapper>
        <StyledInfoWrapper>
          <StyledSubTitle>
            참여한 멘토링 목록 ({participatedMentoringList.length}건)
          </StyledSubTitle>
          <StyledDescription>
            내가 신청한 멘토링 목록을 확인하고 완료된 멘토링에 대해 리뷰를
            작성할 수 있습니다.
          </StyledDescription>
        </StyledInfoWrapper>
        <StyledLine />
        {participatedMentoringList.length > 0 ? (
          <MentoringList>
            {participatedMentoringList.map((item) => (
              <MentoringItem key={item.reservationId} mentoring={item} />
            ))}
          </MentoringList>
        ) : (
          <StyledDescription>참여한 멘토링이 없습니다.</StyledDescription>
        )}
      </StyledWrapper>
    </StyledContainer>
  );
}

export default ParticipatedMentoring;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
`;

const StyledTitle = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 100%;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 2.5rem 2rem;
`;

const StyledSubTitle = styled.h3`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const StyledDescription = styled.p`
  word-break: keep-all;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;

const StyledLine = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: none;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
