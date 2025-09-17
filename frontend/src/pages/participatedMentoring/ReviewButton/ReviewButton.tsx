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
    <S_ReviewWriteButton onClick={onClick} disabled={disabled}>
      리뷰 작성
    </S_ReviewWriteButton>
  );
}

function ReviewCompleteButton() {
  return <S_ReviewCompleteButton>리뷰 완료</S_ReviewCompleteButton>;
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

const S_Container = styled.button`
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

const S_ReviewWriteButton = styled(S_Container)<{ disabled: boolean }>`
  background-color: ${({ theme, disabled }) =>
    disabled ? theme.BG.GRAY : theme.SYSTEM.MAIN700};

  color: ${({ theme, disabled }) =>
    disabled ? theme.FONT.B04 : theme.FONT.W01};

  :hover:not(:disabled) {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const S_ReviewCompleteButton = styled(S_Container)`
  background-color: ${({ theme }) => theme.SYSTEM.MAIN200};

  color: ${({ theme }) => theme.FONT.B02};
  pointer-events: none;
`;
