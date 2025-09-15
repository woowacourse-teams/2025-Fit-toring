import styled from '@emotion/styled';

import { StatusTypeEnum } from '../../../common/types/statusType';

import type { StatusType } from '../../../common/types/statusType';

interface ReviewWriteButtonProps {
  onClick: () => void;
  disabled: boolean;
}

interface ReviewButtonProps {
  isReviewed: boolean;
  status: StatusType;
  onReviewButtonClick: () => void;
}

function ReviewWriteButton({ onClick, disabled }: ReviewWriteButtonProps) {
  return (
    <StyledReviewWriteButton onClick={onClick} disabled={disabled}>
      리뷰 작성
    </StyledReviewWriteButton>
  );
}

function ReviewCompleteButton() {
  return <StyledReviewCompleteButton>리뷰 완료</StyledReviewCompleteButton>;
}

function ReviewButton({
  isReviewed,
  status,
  onReviewButtonClick,
}: ReviewButtonProps) {
  const canWriteReview = !isReviewed && status === StatusTypeEnum.COMPLETE;

  if (isReviewed) {
    return <ReviewCompleteButton />;
  }

  return (
    <ReviewWriteButton
      onClick={onReviewButtonClick}
      disabled={!canWriteReview}
    />
  );
}

export default ReviewButton;

const StyledContainer = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;

  width: 7.5rem;
  margin-left: auto;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 6px;

  transition: all 0.2s ease;

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledReviewWriteButton = styled(StyledContainer)<{ disabled: boolean }>`
  background-color: ${({ theme, disabled }) =>
    disabled ? theme.BG.GRAY : theme.SYSTEM.MAIN700};

  color: ${({ theme, disabled }) =>
    disabled ? theme.FONT.B04 : theme.FONT.W01};

  :hover:not(:disabled) {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const StyledReviewCompleteButton = styled(StyledContainer)`
  background-color: ${({ theme }) => theme.SYSTEM.MAIN200};

  color: ${({ theme }) => theme.FONT.B02};
  pointer-events: none;
`;
