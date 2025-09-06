import type { PropsWithChildren } from 'react';

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

function Step({ type, status, children }: PropsWithChildren<StepProps>) {
  const iconSrc = ICON_MAP[status];

  switch (type) {
    case 'before':
      return (
        <StyledWrapper step="notCurrent">
          <StyledStepCircleWrapper>
            <StyledStepCircle step="before">{children}</StyledStepCircle>
            <StyledLine step="before" />
          </StyledStepCircleWrapper>
        </StyledWrapper>
      );
    case 'current':
      return (
        <StyledWrapper step="current">
          <StyledCurrentCircle>
            <StyledIcon src={iconSrc} alt={`${status} Icon`} />
            {children}
          </StyledCurrentCircle>
        </StyledWrapper>
      );
    case 'after':
      return (
        <StyledWrapper step="notCurrent">
          <StyledStepCircleWrapper>
            <StyledLine step="after" />
            <StyledStepCircle step="after">{children}</StyledStepCircle>
          </StyledStepCircleWrapper>
        </StyledWrapper>
      );
  }
}

export default Step;

const StyledWrapper = styled.div<{ step: 'current' | 'notCurrent' }>`
  display: flex;
  flex-direction: column;
  flex-grow: ${({ step }) => (step === 'notCurrent' ? 1 : 0)};
  gap: 2rem;
`;

const StyledStepCircleWrapper = styled.div`
  display: flex;
  align-items: center;
`;

const StyledStepCircle = styled.div<{ step: 'before' | 'after' }>`
  position: relative;

  width: 2rem;
  height: 2rem;
  border: 2px solid
    ${({ step, theme }) =>
      step === 'before' ? theme.SYSTEM.MAIN600 : theme.SYSTEM.GRAY100};
  border-radius: 50%;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.BG.WHITE : theme.SYSTEM.GRAY100};
`;

const StyledCurrentCircle = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  width: 4rem;
  height: 4rem;
  border-radius: 50%;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const StyledIcon = styled.img`
  display: block;

  width: auto;
  height: 2.2rem;
  aspect-ratio: 1 / 1;
`;

const StyledLine = styled.div<{ step: 'before' | 'after' }>`
  flex-grow: 1;

  width: 3rem;
  height: 2px;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.SYSTEM.MAIN600 : theme.SYSTEM.GRAY100};
`;
