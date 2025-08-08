import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getMentoringApplicationList } from './apis/getMentoringApplicationList';
import MentoringApplicationItem from './components/MentoringApplicationItem/MentoringApplicationItem';
import MentoringApplicationList from './components/MentoringApplicationList/MentoringApplicationList';

import type { MentoringApplication } from './types/mentoringApplication';
import type { StatusType } from '../../common/types/statusType';

function CreatedMentoring() {
  const [mentoringApplicationList, setMentoringApplicationList] = useState<
    MentoringApplication[]
  >([]);

  useEffect(() => {
    const fetchMentoringApplicationList = async () => {
      const response = await getMentoringApplicationList();
      setMentoringApplicationList(response);
    };

    fetchMentoringApplicationList();
  }, []);

  if (!mentoringApplicationList.length) {
    return null;
  }

  const handleActionButtonsClick = ({
    reservationId,
    status,
    phoneNumber,
  }: {
    reservationId: number;
    status: StatusType;
    phoneNumber: string;
  }) => {
    setMentoringApplicationList((prevList) => {
      return prevList.map((item) => {
        if (item.reservationId !== reservationId) {
          return item;
        }
        return {
          ...item,
          status,
          phoneNumber,
        };
      });
    });
  };

  return (
    <StyledContainer>
      <StyledTitle>개설한 멘토링</StyledTitle>
      <StyledWrapper>
        <StyledInfoWrapper>
          <StyledSubTitle>
            멘토링 신청 목록 ({mentoringApplicationList.length}건)
          </StyledSubTitle>
          <StyledDescription>
            사용자들이 신청한 멘토링을 승인하거나 거절할 수 있습니다.
          </StyledDescription>
        </StyledInfoWrapper>
        <StyledLine />
        <MentoringApplicationList>
          {mentoringApplicationList.map((item) => (
            <MentoringApplicationItem
              key={item.reservationId}
              mentoringApplication={item}
              onActionButtonsClick={handleActionButtonsClick}
            />
          ))}
        </MentoringApplicationList>
      </StyledWrapper>
    </StyledContainer>
  );
}

export default CreatedMentoring;

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
