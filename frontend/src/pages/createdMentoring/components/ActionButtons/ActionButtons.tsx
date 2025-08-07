import styled from '@emotion/styled';

import { StatusTypeEnum } from '../../types/statusType';

import type { StatusType } from '../../types/statusType';

interface ActionButtonsProps {
  status: StatusType;
}

function ActionButtons({ status }: ActionButtonsProps) {
  return status === StatusTypeEnum.pending ? (
    <StyledContainer>
      <StyledPrimaryButton>승인</StyledPrimaryButton>
      <StyledSecondaryButton>거절</StyledSecondaryButton>
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
