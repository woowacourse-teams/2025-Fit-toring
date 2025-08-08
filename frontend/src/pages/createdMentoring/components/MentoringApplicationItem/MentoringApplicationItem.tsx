import styled from '@emotion/styled';

import MentoringApplicationStatus from '../../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import { type StatusType } from '../../../../common/types/statusType';
import ActionButtons from '../ActionButtons/ActionButtons';
import PhoneNumber from '../PhoneNumber/PhoneNumber';

import type { MentoringApplication } from '../../types/mentoringApplication';

interface MentoringApplicationItemProps {
  mentoringApplication: MentoringApplication;
  onActionButtonsClick: (params: {
    reservationId: number;
    status: StatusType;
    phoneNumber: string;
  }) => void;
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const fullDate = date.toISOString().split('T')[0];

  return fullDate;
};

const TIME = '15';

function MentoringApplicationItem({
  mentoringApplication: {
    reservationId,
    menteeName,
    phoneNumber,
    price,
    content,
    status,
    createdAt,
  },
  onActionButtonsClick,
}: MentoringApplicationItemProps) {
  const handleActionButtonsComplete = (
    updatedStatus: StatusType,
    phoneNumber: string,
  ) => {
    onActionButtonsClick({
      reservationId,
      status: updatedStatus,
      phoneNumber,
    });
  };

  return (
    <StyledContainer key={reservationId}>
      <StyledName>{menteeName}님의 상담 신청</StyledName>
      <StyledApplicationInfoWrapper>
        <StyledCreatedAt>⏰ {formatDate(createdAt)}</StyledCreatedAt>
        <StyledApplicationPrice>
          💰 {TIME}분 {price.toLocaleString()}원
        </StyledApplicationPrice>
        <MentoringApplicationStatus status={status} />
      </StyledApplicationInfoWrapper>
      <PhoneNumber status={status} phoneNumber={phoneNumber} />
      <StyledApplicationContent>{content}</StyledApplicationContent>
      <ActionButtons
        reservationId={reservationId}
        status={status}
        onClick={handleActionButtonsComplete}
      />
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
    box-shadow: 0 4px 16px rgb(0 0 0 / 10%);
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

const StyledCreatedAt = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationPrice = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledApplicationContent = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
