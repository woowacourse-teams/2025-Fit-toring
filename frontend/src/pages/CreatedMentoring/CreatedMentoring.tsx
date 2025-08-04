import styled from '@emotion/styled';

import MentoringApplicationList from './components/MentoringApplicationList/MentoringApplicationList';

import type { MentoringApplication } from './types/mentoringApplication';

const MENTORING_APPLICATIONS: MentoringApplication[] = [
  {
    id: 1,
    name: '홍길동',
    phoneNumber: null,
    fee: 5000,
    content:
      '다이어트를 위한 운동 계획과 식단 관리에 대해 상담받고 싶습니다. 현재 직장인이라 시간이 제한적인데, 효율적인 운동 방법을 알고 싶어요.',
    status: '승인 대기',
    applicationDate: '2025-01-15',
    scheduledDate: null,
    completionDate: null,
  },
  {
    id: 2,
    name: '김영희',
    phoneNumber: '010-2345-6789',
    fee: 5000,
    content:
      '근육 증가를 위한 식단과 운동 계획에 대해 상담받고 싶습니다. 평일에 짧게 운동할 시간이 있어 효율적인 방법을 찾고 싶어요.',
    status: '승인됨',
    applicationDate: '2025-01-14',
    scheduledDate: '2025-01-21',
    completionDate: null,
  },
  {
    id: 3,
    name: '박철수',
    phoneNumber: null,
    fee: 5000,
    content: '헬스장에서 운동하고 있는데 정체기가 와서 도움이 필요해요.',
    status: '완료됨',
    applicationDate: '2025-01-12',
    scheduledDate: '2025-01-18',
    completionDate: '2025-01-18',
  },
] as const;

function CreatedMentoring() {
  const getStatusImage = (status: string) => {
    switch (status) {
      case '승인 대기':
        return '⏳';
      case '승인됨':
        return '✅';
      case '완료됨':
        return '🎉';
      default:
        return null;
    }
  };

  return (
    <StyledContainer>
      <StyledTitle>개설한 멘토링</StyledTitle>
      <StyledWrapper>
        <StyledInfoWrapper>
          <StyledSubTitle>
            멘토링 신청 목록 ({MENTORING_APPLICATIONS.length}건)
          </StyledSubTitle>
          <StyledDescription>
            사용자들이 신청한 멘토링을 승인하거나 거절할 수 있습니다.
          </StyledDescription>
        </StyledInfoWrapper>
        <StyledLine />
        <MentoringApplicationList>
          {MENTORING_APPLICATIONS.map(
            ({
              id,
              name,
              phoneNumber,
              fee,
              content,
              status,
              applicationDate,
            }) => (
              <StyledApplicationItem key={id}>
                <StyledName>{name}님의 상담 신청</StyledName>
                <StyledApplicationInfoWrapper>
                  <StyledApplicationDate>
                    ⏰ {applicationDate}
                  </StyledApplicationDate>
                  <StyledApplicationFee>
                    💰 15분 {fee.toLocaleString()}원
                  </StyledApplicationFee>
                  <StyledApplicationStatus status={status}>
                    {getStatusImage(status)} {status}
                  </StyledApplicationStatus>
                </StyledApplicationInfoWrapper>
                {phoneNumber && (
                  <StyledApplicationPhoneNumber>
                    연락처: {phoneNumber}
                  </StyledApplicationPhoneNumber>
                )}
                <StyledApplicationContent>{content}</StyledApplicationContent>
                {status === '승인 대기' && (
                  <StyledButtonWrapper>
                    <StyledPrimaryButton>승인</StyledPrimaryButton>
                    <StyledSecondaryButton>거절</StyledSecondaryButton>
                  </StyledButtonWrapper>
                )}
              </StyledApplicationItem>
            ),
          )}
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
  box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);
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

const StyledApplicationItem = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  height: auto;
  padding: 1.5rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  transition: all 0.2s ease;

  :hover {
    box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledName = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;

const StyledApplicationInfoWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
`;

const StyledApplicationDate = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationFee = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationPhoneNumber = styled.p`
  width: fit-content;

  background-color: ${({ theme }) => theme.BG.YELLOW};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationStatus = styled.p<{
  status: '승인 대기' | '승인됨' | '완료됨';
}>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: fit-content;
  padding: 0.3rem 0.6rem;
  border: 1px solid
    ${({ theme, status }) => {
      switch (status) {
        case '승인 대기':
          return theme.OUTLINE.YELLOW;
        case '승인됨':
          return theme.SYSTEM.MAIN300;
        case '완료됨':
          return theme.SYSTEM.MAIN400;
        default:
          return theme.OUTLINE.REGULAR;
      }
    }};
  border-radius: 8px;

  background-color: ${({ theme, status }) => {
    switch (status) {
      case '승인 대기':
        return theme.BG.YELLOW;
      case '승인됨':
        return theme.SYSTEM.MAIN100;
      case '완료됨':
        return theme.SYSTEM.MAIN200;
      default:
        return theme.BG.WHITE;
    }
  }};

  color: ${({ theme, status }) => {
    switch (status) {
      case '승인 대기':
        return theme.FONT.Y01;
      case '승인됨':
      case '완료됨':
        return theme.SYSTEM.MAIN700;
      default:
        return theme.FONT.B04;
    }
  }};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationContent = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledButtonWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const StyledBaseButton = styled.button`
  width: fit-content;
  padding: 0.8rem 1.3rem;
  border: none;
  border-radius: 8px;

  cursor: pointer;

  color: ${({ theme }) => theme.FONT.W01};
  ${({ theme }) => theme.TYPOGRAPHY.BTN4_R}
`;

const StyledPrimaryButton = styled(StyledBaseButton)`
  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const StyledSecondaryButton = styled(StyledBaseButton)`
  background-color: ${({ theme }) => theme.FONT.ERROR};
`;
