import styled from '@emotion/styled';

import approvedIcon from '../../../assets/images/approved.svg';
import completedIcon from '../../../assets/images/completed.svg';
import pendingIcon from '../../../assets/images/pending.svg';

import type { MentoringReservationStatusType } from '../../../types/mentoringReservationStatus';

interface StepProps {
  status: MentoringReservationStatusType;
  type: 'before' | 'current' | 'after';
}

const ICON_MAP: Record<MentoringReservationStatusType, string> = {
  PENDING: pendingIcon,
  APPROVED: approvedIcon,
  COMPLETE: completedIcon,
};

function Step({ type, status }: StepProps) {
  const iconSrc = ICON_MAP[status];

  switch (type) {
    case 'before':
      return (
        <>
          <StyledStepCircle step="before" />
          <StyledLine step="before" />
        </>
      );
    case 'current':
      return (
        <StyledCurrentCircle>
          <StyledIcon src={iconSrc} alt={`${status} Icon`} />
        </StyledCurrentCircle>
      );
    case 'after':
      return (
        <>
          <StyledLine step="after" />
          <StyledStepCircle step="after" />
        </>
      );
  }
}

export default Step;

const StyledStepCircle = styled.div<{ step: 'before' | 'after' }>`
  width: 2rem;
  height: 2rem;
  border: 2px solid
    ${({ step, theme }) =>
      step === 'before' ? theme.FONT.SUCCESS : theme.SYSTEM.GRAY100};
  border-radius: 50%;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.BG.WHITE : theme.SYSTEM.GRAY100};
`;

const StyledCurrentCircle = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 4rem;
  height: 4rem;
  border-radius: 50%;

  background-color: ${({ theme }) => theme.FONT.SUCCESS};
`;

const StyledIcon = styled.img`
  width: 2.2rem;
  height: 2.2rem;
`;

const StyledLine = styled.div<{ step: 'before' | 'after' }>`
  flex-grow: 1;

  width: 3rem;
  height: 2px;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.FONT.SUCCESS : theme.SYSTEM.GRAY100};
`;
