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
        <S_Wrapper step="notCurrent">
          <S_StepCircleWrapper>
            <S_StepCircle step="before">{children}</S_StepCircle>
            <S_Line step="before" />
          </S_StepCircleWrapper>
        </S_Wrapper>
      );
    case 'current':
      return (
        <S_Wrapper step="current">
          <S_CurrentCircle>
            <S_Icon src={iconSrc} alt={`${status} Icon`} />
            {children}
          </S_CurrentCircle>
        </S_Wrapper>
      );
    case 'after':
      return (
        <S_Wrapper step="notCurrent">
          <S_StepCircleWrapper>
            <S_Line step="after" />
            <S_StepCircle step="after">{children}</S_StepCircle>
          </S_StepCircleWrapper>
        </S_Wrapper>
      );
  }
}

export default Step;

const S_Wrapper = styled.div<{ step: 'current' | 'notCurrent' }>`
  display: flex;
  flex-direction: column;
  flex-grow: ${({ step }) => (step === 'notCurrent' ? 1 : 0)};
  gap: 2rem;
`;

const S_StepCircleWrapper = styled.div`
  display: flex;
  align-items: center;
`;

const S_StepCircle = styled.div<{ step: 'before' | 'after' }>`
  position: relative;

  width: 2rem;
  height: 2rem;
  border: 2px solid
    ${({ step, theme }) =>
      step === 'before' ? theme.SYSTEM.MAIN500 : theme.SYSTEM.GRAY100};
  border-radius: 50%;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.BG.WHITE : theme.SYSTEM.GRAY100};
`;

const S_CurrentCircle = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;

  width: 4rem;
  height: 4rem;
  border-radius: 50%;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
`;

const S_Icon = styled.img`
  display: block;

  width: auto;
  height: 2.2rem;
  aspect-ratio: 1 / 1;
`;

const S_Line = styled.div<{ step: 'before' | 'after' }>`
  flex-grow: 1;

  width: 3rem;
  height: 2px;

  background-color: ${({ step, theme }) =>
    step === 'before' ? theme.SYSTEM.MAIN500 : theme.SYSTEM.GRAY100};
`;
