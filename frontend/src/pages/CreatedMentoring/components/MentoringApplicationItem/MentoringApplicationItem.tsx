import styled from '@emotion/styled';

import MentoringApplicationStatus from '../MentoringApplicationStatus/MentoringApplicationStatus';

import type { MentoringApplication } from '../../types/mentoringApplication';

interface MentoringApplicationItemProps {
  mentoringApplication: MentoringApplication;
}

function MentoringApplicationItem({
  mentoringApplication: {
    id,
    name,
    phoneNumber,
    fee,
    content,
    status,
    applicationDate,
  },
}: MentoringApplicationItemProps) {
  return (
    <StyledContainer key={id}>
      <StyledName>{name}님의 상담 신청</StyledName>
      <StyledApplicationInfoWrapper>
        <StyledApplicationDate>⏰ {applicationDate}</StyledApplicationDate>
        <StyledApplicationFee>
          💰 15분 {fee.toLocaleString()}원
        </StyledApplicationFee>
        <MentoringApplicationStatus status={status} />
      </StyledApplicationInfoWrapper>
      {(status === '승인됨' || status === '완료됨') && (
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
    </StyledContainer>
  );
}

export default MentoringApplicationItem;

const StyledContainer = styled.li`
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
