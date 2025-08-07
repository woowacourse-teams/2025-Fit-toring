import styled from '@emotion/styled';

import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';
import { patchReservationStatus } from '../../apis/patchReservationStatus';
import { MENTORING_APPLICATION_STATUS_ENUM } from '../../types/mentoringApplicationStatus';

import type { MENTORING_APPLICATION_STATUS } from '../../types/mentoringApplicationStatus';

interface ActionButtonsProps {
  reservationId: number;
  status: StatusType;
  onClick: (status: StatusType, phoneNumber: string) => void;
}

function ActionButtons({ reservationId, status, onClick }: ActionButtonsProps) {
  const handleActionButtonClick = async (
    newStatus: MENTORING_APPLICATION_STATUS,
  ) => {
    try {
      const response = await patchReservationStatus(reservationId, {
        status: newStatus,
      });

      if (response.status === 200) {
        const updatedStatus =
          newStatus === MENTORING_APPLICATION_STATUS_ENUM.APPROVE
            ? StatusTypeEnum.approved
            : StatusTypeEnum.rejected;

        onClick(updatedStatus, '010-1111-2222');
      }
    } catch (error) {
      console.error(`Error ${newStatus} reservation:`, error);
      return;
    }
  };

  return status === StatusTypeEnum.pending ? (
    <StyledContainer>
      <StyledPrimaryButton
        onClick={() =>
          handleActionButtonClick(MENTORING_APPLICATION_STATUS_ENUM.APPROVE)
        }
      >
        승인
      </StyledPrimaryButton>
      <StyledSecondaryButton
        onClick={() =>
          handleActionButtonClick(MENTORING_APPLICATION_STATUS_ENUM.REJECT)
        }
      >
        거절
      </StyledSecondaryButton>
    </StyledContainer>
  ) : null;
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
