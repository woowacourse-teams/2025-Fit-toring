import styled from '@emotion/styled';

import ActionButtons from '../ActionButtons/ActionButtons';
import MentoringApplicationStatus from '../MentoringApplicationStatus/MentoringApplicationStatus';
import PhoneNumber from '../PhoneNumber/PhoneNumber';

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
  const TIME = '15';
  return (
    <StyledContainer key={id}>
      <StyledName>{name}님의 상담 신청</StyledName>
      <StyledApplicationInfoWrapper>
        <StyledApplicationDate>⏰ {applicationDate}</StyledApplicationDate>
        <StyledApplicationFee>
          💰 {TIME}분 {fee.toLocaleString()}원
        </StyledApplicationFee>
        <MentoringApplicationStatus status={status} />
      </StyledApplicationInfoWrapper>
      <PhoneNumber status={status} phoneNumber={phoneNumber} />
      <StyledApplicationContent>{content}</StyledApplicationContent>
      <ActionButtons status={status} />
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

const StyledApplicationContent = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
