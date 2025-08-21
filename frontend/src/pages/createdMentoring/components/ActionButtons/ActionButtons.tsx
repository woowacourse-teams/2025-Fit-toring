import styled from '@emotion/styled';

import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';
import { getMenteePhoneNumber } from '../../apis/getMenteePhoneNumber';
import { patchReservationStatus } from '../../apis/patchReservationStatus';
import { MENTORING_APPLICATION_STATUS_ENUM } from '../../types/mentoringApplicationStatus';

import type { MENTORING_APPLICATION_STATUS } from '../../types/mentoringApplicationStatus';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

interface ActionButtonsProps {
  reservationId: number;
  status: StatusType;
  onClick: (status: StatusType, phoneNumber: string) => void;
}

function ActionButtons({ reservationId, status, onClick }: ActionButtonsProps) {
  const fetchPhoneNumber = async (status: StatusType) => {
    try {
      const { phoneNumber } = await getMenteePhoneNumber(reservationId);

      onClick(status, phoneNumber);
    } catch (error) {
      console.error(`Error fetching mentee phone number:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'mentee-phone-number-fetch',
      });
    }
  };

  const updateStatus = async (newStatus: MENTORING_APPLICATION_STATUS) => {
    try {
      const response = await patchReservationStatus(reservationId, {
        status: newStatus,
      });

      if (response.status !== 200) throw new Error('status update failed');
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
      await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.APPROVED);
      await fetchPhoneNumber(StatusTypeEnum.APPROVED);
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
      await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.REJECTED);
      onClick(StatusTypeEnum.REJECTED, '');
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
      await updateStatus(MENTORING_APPLICATION_STATUS_ENUM.COMPLETE);
      onClick(StatusTypeEnum.COMPLETE, '');
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
      <StyledContainer>
        <StyledPrimaryButton onClick={handleApproveButtonClick}>
          승인
        </StyledPrimaryButton>
        <StyledSecondaryButton onClick={handleRejectedButtonClick}>
          거절
        </StyledSecondaryButton>
      </StyledContainer>
    );
  }
  if (status === StatusTypeEnum.APPROVED) {
    return (
      <StyledContainer>
        <StyledPrimaryButton onClick={handleCompleteButtonClick}>
          완료
        </StyledPrimaryButton>
      </StyledContainer>
    );
  }
}

export default ActionButtons;

const StyledContainer = styled.div`
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
  background-color: ${({ theme }) => theme.SYSTEM.MAIN700};
`;

const StyledSecondaryButton = styled(StyledBaseButton)`
  background-color: ${({ theme }) => theme.BG.RED};
`;
