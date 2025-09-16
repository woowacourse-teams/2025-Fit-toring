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
    <S_Container key={reservationId}>
      <S_Name>{menteeName}님의 상담 신청</S_Name>
      <S_ApplicationInfoWrapper>
        <S_CreatedAt>⏰ {formatDate(createdAt)}</S_CreatedAt>
        <S_ApplicationPrice>
          💰 {TIME}분 {price.toLocaleString()}원
        </S_ApplicationPrice>
        <MentoringApplicationStatus status={status} />
      </S_ApplicationInfoWrapper>
      <PhoneNumber status={status} phoneNumber={phoneNumber} />
      <S_ApplicationContent>{content}</S_ApplicationContent>
      <S_ButtonWrapper>
        <ActionButtons
          reservationId={reservationId}
          status={status}
          onClick={handleActionButtonsComplete}
        />
      </S_ButtonWrapper>
    </S_Container>
  );
}

export default MentoringApplicationItem;

const S_Container = styled.li`
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

const S_Name = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;

const S_ApplicationInfoWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
`;

const S_CreatedAt = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ApplicationPrice = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ApplicationContent = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
`;
