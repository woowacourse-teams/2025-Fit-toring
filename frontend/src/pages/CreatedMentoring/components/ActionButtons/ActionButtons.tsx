import styled from '@emotion/styled';

interface ActionButtonsProps {
  status: '승인 대기' | '승인됨' | '완료됨' | '거절됨';
}

function ActionButtons({ status }: ActionButtonsProps) {
  return status === '승인 대기' ? (
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
  background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;

const StyledSecondaryButton = styled(StyledBaseButton)`
  background-color: ${({ theme }) => theme.FONT.ERROR};
`;
