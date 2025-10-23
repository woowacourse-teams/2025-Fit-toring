import styled from '@emotion/styled';

import tooltipIcon from '../../../assets/images/tooltip.svg';
import {
  MentoringReservationStatusTypeEnum,
  type MentoringReservationStatusType,
} from '../../../types/mentoringReservationStatus';
import Step from '../Step/Step';
import { useState } from 'react';

interface MentoringStepperProps {
  status: MentoringReservationStatusType;
}

const statusTextMap = {
  [MentoringReservationStatusTypeEnum.PENDING]: '예약신청',
  [MentoringReservationStatusTypeEnum.APPROVED]: '신청확정',
  [MentoringReservationStatusTypeEnum.COMPLETE]: '멘토링완료',
} as const;

const statusInfoTextMap: Record<MentoringReservationStatusTypeEnum, string> = {
  PENDING: '예약 승인 시 문자로 연락용 오픈카톡방 링크가 발송됩니다.',
  APPROVED:
    '예약이 확정되었습니다. 문자로 발송된 오픈카톡 링크를 통해 멘토와 대화를 시작할 수 있습니다.',
  COMPLETE: '멘토링이 완료되었습니다.',
} as const;

function MentoringStepper({ status }: MentoringStepperProps) {
  const stepValues = Object.values(
    MentoringReservationStatusTypeEnum,
  ) as MentoringReservationStatusType[];
  const currentStep = stepValues.indexOf(status);
  const [opened, setOpened] = useState(false);

  const handleTooltipToggle = (e: React.MouseEvent<HTMLImageElement>) => {
    e.stopPropagation();
    setOpened((prev) => !prev);
  };

  const getType = (step: number) => {
    if (step > currentStep) {
      return 'after';
    }

    if (step === currentStep) {
      return 'current';
    }

    return 'before';
  };

  return (
    <S_Steps>
      {stepValues.map((stepInfo, step) => (
        <Step type={getType(step)} status={status} key={stepInfo}>
          <S_TextWrapper>
            <S_Text step={getType(step)}>{statusTextMap[stepInfo]}</S_Text>
            <S_IconWrapper>
              <S_Icon
                src={tooltipIcon}
                alt="툴팁 아이콘"
                onClick={handleTooltipToggle}
              />
              <S_Tooltip opened={opened}>
                {statusInfoTextMap[stepInfo]}
              </S_Tooltip>
            </S_IconWrapper>
          </S_TextWrapper>
        </Step>
      ))}
    </S_Steps>
  );
}

export default MentoringStepper;

const S_Steps = styled.div`
  display: flex;
  align-items: center;

  width: 90%;
  margin-top: 2.6rem;
  margin-bottom: 1rem;
`;

const S_TextWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  margin-top: 3.2rem;
`;

const S_Text = styled.span<{ step: 'before' | 'current' | 'after' }>`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.C4_R}
  white-space: nowrap;
`;

const S_Icon = styled.img`
  width: 1rem;
  height: 1rem;
  cursor: pointer;
`;

const S_IconWrapper = styled.div`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;

  &:hover > div {
    opacity: 1;

    visibility: visible;
    transform: translate(-50%, 0); /* hover 시 살짝 올라오는 애니메이션 */
  }
`;

const S_Tooltip = styled.div<{ opened: boolean }>`
  visibility: ${({ opened }) => (opened ? 'visible' : 'hidden')};
  position: absolute;
  top: 2rem;
  left: 50%;

  padding: 0.8rem;
  border-radius: 0.4rem;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY800};
  ${({ theme }) => theme.TYPOGRAPHY.C2_R}

  color: ${({ theme }) => theme.BG.WHITE};
  white-space: nowrap;
  transform: translate(-50%, 10px);

  opacity: 0;
  transition: all 0.2s ease-in-out;
  pointer-events: none;

  &::after {
    position: absolute;
    top: -3px;
    left: 50%;

    border-right: 4px solid transparent;
    border-bottom: 5px solid ${({ theme }) => theme.SYSTEM.GRAY800}; /* 꼬리 색 */
    border-left: 4px solid transparent;
    content: '';
    transform: translateX(-50%);
  }
`;
