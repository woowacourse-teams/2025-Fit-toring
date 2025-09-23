import styled from '@emotion/styled';

import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { patchReservationStatus } from '../../apis/patchReservationStatus';
import { MENTORING_APPLICATION_STATUS_ENUM } from '../../types/mentoringApplicationStatus';

import type { MENTORING_APPLICATION_STATUS } from '../../types/mentoringApplicationStatus';

interface ActionButtonsProps {
  reservationId: number;
  status: StatusType;
  onClick: (status: StatusType) => void;
}

function ActionButtons({ reservationId, status, onClick }: ActionButtonsProps) {
  const updateStatus = async (newStatus: MENTORING_APPLICATION_STATUS) => {
    try {
      const response = await patchReservationStatus(reservationId, {
        status: newStatus,
      });

      if (response.status !== 200) {
        throw new Error('status update failed');
      }
    } catch (error) {
      console.error(`Error updating reservation status:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'patch-reservation-status',
      });
    }
  };

  const handleApproveButtonClick = async () => {
    try {
      if (
        confirm('한번 승인한 후에는 취소할 수 없습니다. 정말 승인하시겠습니까?')
      ) {
        await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.APPROVED);
        onClick(MENTORING_APPLICATION_STATUS_ENUM.APPROVED);
      }
    } catch (error) {
      console.error(`Error handling approve button click:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'approve-button-click',
      });
    }
  };

  const handleRejectedButtonClick = async () => {
    try {
      if (
        confirm('한번 거절한 후에는 취소할 수 없습니다. 정말 거절하시겠습니까?')
      ) {
        await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.REJECTED);
        onClick(StatusTypeEnum.REJECTED);
      }
    } catch (error) {
      console.error(`Error handling reject button click:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'reject-button-click',
      });
    }
  };

  const handleCompleteButtonClick = async () => {
    try {
      if (
        confirm('한번 완료한 후에는 취소할 수 없습니다. 정말 완료하시겠습니까?')
      ) {
        await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.COMPLETE);
        onClick(StatusTypeEnum.COMPLETE);
      }
    } catch (error) {
      console.error(`Error handling complete button click:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'complete-button-click',
      });
    }
  };

  if (status === StatusTypeEnum.PENDING) {
    return (
      <S_Container>
        <S_SecondaryButton onClick={handleRejectedButtonClick}>
          거절
        </S_SecondaryButton>
        <S_PrimaryButton onClick={handleApproveButtonClick}>
          승인
        </S_PrimaryButton>
      </S_Container>
    );
  }
  if (status === StatusTypeEnum.APPROVED) {
    return (
      <S_Container>
        <S_PrimaryButton onClick={handleCompleteButtonClick}>
          완료
        </S_PrimaryButton>
      </S_Container>
    );
  }
}

export default ActionButtons;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;

  width: 100%;
`;

const S_BaseButton = styled.button`
  width: 100%;
  height: 3.6rem;
  padding: 0.8rem 1.3rem;
  border: none;
  border-radius: 8px;

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.BTN2_R}
`;

const S_PrimaryButton = styled(S_BaseButton)`
  background-color: ${({ theme }) => theme.SYSTEM.GRAY900};

  color: ${({ theme }) => theme.FONT.W01};
`;

const S_SecondaryButton = styled(S_BaseButton)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.BLACK};

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
`;
