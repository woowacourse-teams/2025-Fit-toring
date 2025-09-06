import styled from '@emotion/styled';

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
        acc[key] = '승인 대기';
        break;
      case 'APPROVED':
        acc[key] = '승인 완료';
        break;
      case 'COMPLETE':
        acc[key] = '멘토링 완료';
        break;
    }
    return acc;
  },
  {} as Record<string, string>,
);

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
              <StyledText step={getType(stepInfo.step)}>
                {statusTextMap[stepInfo.status]}
              </StyledText>
            </Step>
          </>
        ))}
      </StyledSteps>
    </StyledContainer>
  );
}

export default MentoringStepper;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
`;

const StyledSteps = styled.div`
  display: flex;
  align-items: center;

  width: 95%;
`;

const StyledText = styled.span<{ step: 'before' | 'current' | 'after' }>`
  position: absolute;
  top: 50%;
  left: 50%;

  margin-top: 3.2rem;

  color: ${({ step, theme }) =>
    step === 'after' ? theme.SYSTEM.GRAY100 : theme.FONT.SUCCESS};
  white-space: nowrap;
  transform: translate(-50%, -50%);
`;
