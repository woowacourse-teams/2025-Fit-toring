import styled from '@emotion/styled';

import tooltipIcon from '../../../assets/images/tooltip.svg';
import {
  MentoringReservationStatusTypeEnum,
  type MentoringReservationStatusType,
} from '../../../types/mentoringReservationStatus';
import Step from '../Step/Step';

interface MentoringStepperProps {
  status: MentoringReservationStatusType;
}

const statusTextMap = Object.values(MentoringReservationStatusTypeEnum).reduce(
  (acc, key) => {
    switch (key) {
      case 'PENDING':
        acc[key] = '예약신청';
        break;
      case 'APPROVED':
        acc[key] = '신청확정';
        break;
      case 'COMPLETE':
        acc[key] = '멘토링완료';
        break;
    }
    return acc;
  },
  {} as Record<string, string>,
);

const statusInfoTextMap: Record<MentoringReservationStatusTypeEnum, string> = {
  PENDING: '예약 확정 완료 시 문자로 연락용 오픈카톡방 링크가 발송됩니다.',
  APPROVED:
    '신청이 확정되었습니다. 문자로 발송된 오픈카톡 링크를 통해 멘토와 대화를 시작할 수 있습니다.',
  COMPLETE: '멘토링이 완료되었습니다.',
};

function MentoringStepper({ status }: MentoringStepperProps) {
  const stepValues = Object.values(MentoringReservationStatusTypeEnum);

  const stepOrder = stepValues.map((value, index) => ({
    step: index,
    status: value,
  }));

  const matchedStatus = stepOrder.find((item) => item.status === status);
  const currentStep = matchedStatus ? matchedStatus.step : 0;

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
    <StyledContainer>
      <StyledSteps>
        {stepOrder.map((stepInfo) => (
          <>
            <Step
              type={getType(stepInfo.step)}
              status={status}
              key={stepInfo.status}
            >
              <StyledTextWrapper>
                <StyledText step={getType(stepInfo.step)}>
                  {statusTextMap[stepInfo.status]}
                </StyledText>
                <StyledIconWrapper>
                  <StyledIcon src={tooltipIcon} alt="툴팁 아이콘" />
                  <StyledTooltip>
                    {statusInfoTextMap[stepInfo.status]}
                  </StyledTooltip>
                </StyledIconWrapper>
              </StyledTextWrapper>
            </Step>
          </>
        ))}
      </StyledSteps>
    </StyledContainer>
  );
}

export default MentoringStepper;

const StyledContainer = styled.div`
  --circle-size: 2rem;
  --offset: 3.2rem;
  --text-height: 1rem;

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;

  padding-bottom: calc(
    var(--circle-size) / 2 + var(--offset) - var(--circle-size)
  );
`;

const StyledSteps = styled.div`
  display: flex;
  align-items: center;

  width: 100%;
`;

const StyledTextWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  margin-top: 3.2rem;
`;

const StyledText = styled.span<{ step: 'before' | 'current' | 'after' }>`
  color: ${({ step, theme }) =>
    step === 'after' ? theme.SYSTEM.GRAY200 : theme.SYSTEM.MAIN500};
  white-space: nowrap;
`;

const StyledIcon = styled.img`
  width: 1rem;
  height: 1rem;
  cursor: pointer;
`;

const StyledIconWrapper = styled.div`
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

const StyledTooltip = styled.div`
  visibility: hidden;
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
